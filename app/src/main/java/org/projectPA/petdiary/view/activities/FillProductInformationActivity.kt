package org.projectPA.petdiary.view.activities

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            viewModel.imageUri.value?.let { uri ->
                binding.productImage.setImageURI(uri)
                viewModel.validateInputs(
                    binding.formInputBrandName.text.toString().trim(),
                    binding.formInputProductName.text.toString().trim(),
                    binding.formInputDescription.text.toString().trim()
                )
            }
        }
    }

    private val pickPhotoLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            viewModel.setImageUri(it)
            binding.productImage.setImageURI(it)
            viewModel.validateInputs(
                binding.formInputBrandName.text.toString().trim(),
                binding.formInputProductName.text.toString().trim(),
                binding.formInputDescription.text.toString().trim()
            )
        }
    }

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
            val options = arrayOf("Take Picture", "Choose from Gallery", "Upload File")
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Select Image")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> takePicture()
                        1 -> pickPhoto()
                        2 -> pickFile()
                    }
                }
            builder.show()
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
            binding.productImage.setImageURI(uri)
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

        viewModel.navigateToProductDetail.observe(this, Observer { productId ->
            productId?.let {
                val intent = Intent(this, ProductDetailActivity::class.java).apply {
                    putExtra("productId", it)
                    putExtra("fromFillProductInfo", true)
                }
                startActivity(intent)
                finish()
            }
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

    private fun takePicture() {
        val uri = viewModel.createImageUri(this)
        uri?.let {
            viewModel.setImageUri(it)
            takePictureLauncher.launch(it)
        }
    }

    private fun pickPhoto() {
        pickPhotoLauncher.launch("image/*")
    }

    private fun pickFile() {
        pickPhotoLauncher.launch("*/*")
    }

    companion object {
        private const val TAG = "FillProductInformationActivity"
        private const val PICK_IMAGE_REQUEST = 1
    }
}
