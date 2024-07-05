package org.projectPA.petdiary.view.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
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
                binding.productImage.setImageURI(uri)
            }
        }
        viewModel.validateInputs(
            binding.formInputBrandName.text.toString().trim(),
            binding.formInputProductName.text.toString().trim(),
            binding.formInputDescription.text.toString().trim()
        )
    }

    private val pickPhotoLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            viewModel.setImageUri(it)
            binding.productImage.setImageURI(it)
        }
        viewModel.validateInputs(
            binding.formInputBrandName.text.toString().trim(),
            binding.formInputProductName.text.toString().trim(),
            binding.formInputDescription.text.toString().trim()
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
        binding.uploadPhotoButton.setOnClickListener {
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

        setupHintVisibility(binding.formInputBrandName, binding.brandNameLayout)
        setupHintVisibility(binding.formInputProductName, binding.productNameLayout)
        setupHintVisibility(binding.formInputDescription, binding.descriptionLayout)

        binding.formInputBrandName.addTextChangedListener { text ->
            viewModel.validateInputs(
                text.toString().trim(),
                binding.formInputProductName.text.toString().trim(),
                binding.formInputDescription.text.toString().trim()
            )
            viewModel.checkProductNameExists(
                text.toString().trim(),
                binding.formInputProductName.text.toString().trim()
            )
        }
        binding.formInputProductName.addTextChangedListener { text ->
            val productName = text.toString().trim()
            viewModel.checkProductNameExists(
                binding.formInputBrandName.text.toString().trim(),
                productName
            )
            viewModel.validateInputs(
                binding.formInputBrandName.text.toString().trim(),
                productName,
                binding.formInputDescription.text.toString().trim()
            )
        }

        binding.formInputDescription.addTextChangedListener { text ->
            if (text != null) {
                if (text.length < 30) {
                    binding.warningDescription.visibility = View.VISIBLE
                } else {
                    binding.warningDescription.visibility = View.GONE
                }
            }
            viewModel.validateInputs(
                binding.formInputBrandName.text.toString().trim(),
                binding.formInputProductName.text.toString().trim(),
                text.toString().trim()
            )
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
            binding.submitButton.isEnabled = false
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
            binding.warningProductNameAlreadyExist2.visibility = if (error == true) View.VISIBLE else View.GONE
            viewModel.validateInputs(
                binding.formInputBrandName.text.toString().trim(),
                binding.formInputProductName.text.toString().trim(),
                binding.formInputDescription.text.toString().trim()
            )
        })

        viewModel.navigateToProductDetail.observe(this, Observer { productId ->
            productId?.let {
                val intent = Intent(this, ProductDetailActivity::class.java).apply {
                    putExtra("productId", it)
                }
                startActivity(intent)
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