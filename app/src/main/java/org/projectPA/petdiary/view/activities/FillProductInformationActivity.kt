package org.projectPA.petdiary.view.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ActivityFillProductInformationBinding
import org.projectPA.petdiary.viewmodel.FillProductInformationViewModel

class FillProductInformationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFillProductInformationBinding
    private val viewModel: FillProductInformationViewModel by viewModels()
    private val PET_TYPE_KEY = "pet_type"
    private val CATEGORY_KEY = "category"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFillProductInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.uploadPhotoButton.setOnClickListener {
            chooseImage()
        }

        setupHintVisibility(binding.formInputBrandName, binding.brandNameLayout)
        setupHintVisibility(binding.formInputProductName, binding.productNameLayout)
        setupHintVisibility(binding.formInputDescription, binding.descriptionLayout)

        // Add text changed listeners to validate inputs on the fly
        binding.formInputBrandName.addTextChangedListener { text ->
            viewModel.validateInputs(text.toString().trim(), binding.formInputProductName.text.toString().trim(), binding.formInputDescription.text.toString().trim())
        }
        binding.formInputProductName.addTextChangedListener { text ->
            val productName = text.toString().trim()
            viewModel.checkProductNameExists(productName)
        }
        binding.formInputDescription.addTextChangedListener { text ->
            viewModel.validateInputs(binding.formInputBrandName.text.toString().trim(), binding.formInputProductName.text.toString().trim(), text.toString().trim())
        }

        binding.submitButton.setOnClickListener {
            if (binding.submitButton.isEnabled) {
                val brandName = binding.formInputBrandName.text.toString().trim()
                val productName = binding.formInputProductName.text.toString().trim()
                val description = binding.formInputDescription.text.toString().trim()
                val petType = intent.getStringExtra(PET_TYPE_KEY) ?: ""
                val category = intent.getStringExtra(CATEGORY_KEY) ?: ""

                viewModel.uploadData(this, brandName, productName, description, petType, category)
            }
        }
    }

    private fun setupObservers() {
        viewModel.imageUri.observe(this, Observer { uri ->
            binding.uploadPhotoButton.setImageURI(uri)
        })

        viewModel.isFormValid.observe(this, Observer { isValid ->
            binding.submitButton.isEnabled = isValid
        })

        viewModel.uploadStatus.observe(this, Observer { status ->
            Toast.makeText(this, status, Toast.LENGTH_SHORT).show()
        })

        viewModel.productNameError.observe(this, Observer { error ->
            binding.warningProductNameAlreadyExist.visibility = if (error == true) View.VISIBLE else View.GONE
        })
    }

    private fun setupHintVisibility(editText: TextInputEditText, textInputLayout: TextInputLayout) {
        editText.addTextChangedListener { text ->
            if (text.isNullOrEmpty()) {
                textInputLayout.hint = null
            } else {
                textInputLayout.hint = when (editText.id) {
                    R.id.formInputBrandName -> "Brand Name"
                    R.id.formInputProductName -> "Product Name"
                    R.id.formInputDescription -> "Description"
                    else -> null
                }
            }
        }
    }

    private fun chooseImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            viewModel.setImageUri(data.data!!)
        } else {
            Toast.makeText(this, "Image pick cancelled or failed", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val TAG = "FillProductInformationActivity"
        private const val PICK_IMAGE_REQUEST = 1
    }
}
