package org.projectPA.petdiary.view.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import java.util.Locale

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
            viewModel.updateBrandName(text.toString().trim())
        }
        binding.formInputProductName.addTextChangedListener { text ->
            viewModel.updateProductName(text.toString().trim())
        }
        binding.formInputDescription.addTextChangedListener { text ->
            viewModel.updateDescription(text.toString().trim())
        }

        binding.submitButton.setOnClickListener {
            if (binding.submitButton.isEnabled) {
                val brandName = binding.formInputBrandName.text.toString().trim().capitalizeEachWord()
                val productName = binding.formInputProductName.text.toString().trim().capitalizeEachWord()
                val description = binding.formInputDescription.text.toString().trim()
                val petType = intent.getStringExtra(PET_TYPE_KEY) ?: ""
                val category = intent.getStringExtra(CATEGORY_KEY) ?: ""

                viewModel.uploadData(this, brandName, productName, description, petType, category)
            }
        }
    }
    // Extension function to capitalize each word in a string
    fun String.capitalizeEachWord(): String = split(" ").joinToString(" ") { it.lowercase().replaceFirstChar { char -> char.uppercase() } }

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
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Choose from Files")

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Select Image")
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Take Photo" -> {
                    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    try {
                        startActivityForResult(takePictureIntent, PICK_IMAGE_REQUEST)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(this, "Error opening camera", Toast.LENGTH_SHORT).show()
                    }
                }
                options[item] == "Choose from Gallery" -> {
                    val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    pickPhoto.type = "image/*"
                    startActivityForResult(pickPhoto, PICK_IMAGE_REQUEST)
                }
                options[item] == "Choose from Files" -> {
                    val pickFile = Intent(Intent.ACTION_GET_CONTENT)
                    pickFile.type = "image/*"
                    startActivityForResult(pickFile, PICK_IMAGE_REQUEST)
                }
            }
        }
        builder.show()
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
