package org.projectPA.petdiary.view.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
    private val CAMERA_PERMISSION_REQUEST_CODE = 1001

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            viewModel.imageUri.value?.let { uri ->
                binding.uploadProductImage.setImageURI(uri)
            }
        }
        viewModel.validateInputs(
            binding.inputBrandName.text.toString().trim(),
            binding.inputNameProduct.text.toString().trim(),
            binding.inputDescriptionProduct.text.toString().trim(),
            binding.inputReview.text.toString().trim(),
            binding.ratingBar.rating,
            binding.usageDropdown.selectedItem.toString(),
            binding.icThumbsUpInactive.visibility == View.VISIBLE
        )
    }

    private val pickPhotoLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            viewModel.setImageUri(it)
            binding.uploadProductImage.setImageURI(it)
        }
        viewModel.validateInputs(
            binding.inputBrandName.text.toString().trim(),
            binding.inputNameProduct.text.toString().trim(),
            binding.inputDescriptionProduct.text.toString().trim(),
            binding.inputReview.text.toString().trim(),
            binding.ratingBar.rating,
            binding.usageDropdown.selectedItem.toString(),
            binding.icThumbsUpInactive.visibility == View.VISIBLE
        )
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
        binding.uploadProductImage.setOnClickListener {
            val options = arrayOf("Take Picture", "Choose from Gallery")
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Select Image")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> checkAndRequestCameraPermission()
                        1 -> pickPhoto()
                    }
                }
            builder.show()
        }

        setupHintVisibility(binding.inputBrandName, binding.formInputBrandName)
        setupHintVisibility(binding.inputNameProduct, binding.formInputNameProduct)
        setupHintVisibility(binding.inputDescriptionProduct, binding.formInputDescriptionProduct)

        binding.inputBrandName.addTextChangedListener { text ->
            viewModel.validateInputs(
                text.toString().trim(),
                binding.inputNameProduct.text.toString().trim(),
                binding.inputDescriptionProduct.text.toString().trim(),
                binding.inputReview.text.toString().trim(),
                binding.ratingBar.rating,
                binding.usageDropdown.selectedItem.toString(),
                binding.icThumbsUpInactive.visibility == View.VISIBLE
            )
            viewModel.checkProductNameExists(
                text.toString().trim(),
                binding.inputNameProduct.text.toString().trim()
            )
        }
        binding.inputNameProduct.addTextChangedListener { text ->
            val productName = text.toString().trim()
            viewModel.checkProductNameExists(
                binding.inputBrandName.text.toString().trim(),
                productName
            )
            viewModel.validateInputs(
                binding.inputBrandName.text.toString().trim(),
                productName,
                binding.inputDescriptionProduct.text.toString().trim(),
                binding.inputReview.text.toString().trim(),
                binding.ratingBar.rating,
                binding.usageDropdown.selectedItem.toString(),
                binding.icThumbsUpInactive.visibility == View.VISIBLE
            )
        }

        binding.inputDescriptionProduct.addTextChangedListener { text ->
            if (text != null) {
                if (text.length < 30) {
                    binding.warningDescription.visibility = View.VISIBLE
                } else {
                    binding.warningDescription.visibility = View.GONE
                }
            }
            viewModel.validateInputs(
                binding.inputBrandName.text.toString().trim(),
                binding.inputNameProduct.text.toString().trim(),
                text.toString().trim(),
                binding.inputReview.text.toString().trim(),
                binding.ratingBar.rating,
                binding.usageDropdown.selectedItem.toString(),
                binding.icThumbsUpInactive.visibility == View.VISIBLE
            )
        }

        binding.submitButton.setOnClickListener {
            if (binding.submitButton.isEnabled) {
                binding.progressBar.visibility = View.VISIBLE
                val brandName = binding.inputBrandName.text.toString().trim()
                val productName = binding.inputNameProduct.text.toString().trim()
                val description = binding.inputDescriptionProduct.text.toString().trim()
                val petType = intent.getStringExtra(PET_TYPE_KEY) ?: ""
                val category = intent.getStringExtra(CATEGORY_KEY) ?: ""
                val review = binding.inputReview.text.toString().trim()
                val rating = binding.ratingBar.rating
                val usage = binding.usageDropdown.selectedItem.toString()
                val recommend = binding.icThumbsUpInactive.visibility == View.VISIBLE

                Log.d("FillProductInformationActivity", "Submitting data...")
                viewModel.uploadData(this, brandName, productName, description, petType, category, review, rating, usage, recommend)
            }
            binding.submitButton.isEnabled = false
        }

    }

    private fun setupObservers() {
        viewModel.imageUri.observe(this, Observer { uri ->
            binding.uploadProductImage.setImageURI(uri)
        })

        viewModel.isFormValid.observe(this, Observer { isValid ->
            binding.submitButton.isEnabled = isValid
        })

        viewModel.uploadStatus.observe(this, Observer { status ->
            Toast.makeText(this, status, Toast.LENGTH_SHORT).show()
            binding.progressBar.visibility = View.GONE
        })

        viewModel.productNameError.observe(this, Observer { error ->
            binding.warningProductNameAlreadyExist.visibility = if (error == true) View.VISIBLE else View.GONE
            viewModel.validateInputs(
                binding.inputBrandName.text.toString().trim(),
                binding.inputNameProduct.text.toString().trim(),
                binding.inputDescriptionProduct.text.toString().trim(),
                binding.inputReview.text.toString().trim(),
                binding.ratingBar.rating,
                binding.usageDropdown.selectedItem.toString(),
                binding.icThumbsUpInactive.visibility == View.VISIBLE
            )
        })

        viewModel.navigateToProductDetail.observe(this, Observer { productId ->
            productId?.let {
                val intent = Intent(this, ProductDetailActivity::class.java).apply {
                    putExtra("productId", it)
                }
                Log.d("FillProductInformationActivity", "Navigating to ProductDetailActivity with productId: $it")
                startActivity(intent)
                binding.progressBar.visibility = View.GONE  // Hide progress bar after navigation
            }
        })
    }

    private fun setupHintVisibility(editText: TextInputEditText, textInputLayout: TextInputLayout) {
        editText.addTextChangedListener { text ->
            if (text.isNullOrEmpty()) {
                textInputLayout.hint = null
            } else {
                textInputLayout.hint = when (editText.id) {
                    R.id.input_brand_name -> "Brand Name"
                    R.id.input_name_product -> "Product Name"
                    R.id.input_description_product -> "Description"
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

    companion object {
        private const val TAG = "FillProductInformationActivity"
    }

    private fun checkAndRequestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            takePicture()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture()
            } else {
                Toast.makeText(this, "Camera permission is required to take pictures", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
