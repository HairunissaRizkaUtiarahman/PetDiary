package org.projectPA.petdiary.ui.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.projectPA.petdiary.databinding.ActivityFillProductInformationBinding
import org.projectPA.petdiary.model.Product
import java.util.*

class FillProductInformationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFillProductInformationBinding
    private var imageUri: Uri? = null
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
    }

    private fun setupListeners() {
        binding.uploadPhotoButton.setOnClickListener {
            chooseImage()
        }

        // Add text changed listeners to validate inputs on the fly
        binding.formInputBrandName.addTextChangedListener { validateInputs() }
        binding.formInputProductName.addTextChangedListener { validateInputs() }
        binding.formInputDescription.addTextChangedListener { validateInputs() }

        binding.submitButton.setOnClickListener {
            if (binding.submitButton.isEnabled) {
                uploadData()
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
            imageUri = data.data
            binding.uploadPhotoButton.setImageURI(imageUri)  // Display selected image on button
            validateInputs()  // Re-validate inputs after image is picked
        } else {
            Toast.makeText(this, "Image pick cancelled or failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInputs() {
        val brandName = binding.formInputBrandName.text.toString().trim()
        val productName = binding.formInputProductName.text.toString().trim()
        val description = binding.formInputDescription.text.toString().trim()

        // Check conditions for all inputs including the image URI
        val isFormValid = brandName.length <= 30 &&
                productName.length in 5..30 &&
                description.length >= 50 &&
                imageUri != null

        // Enable the submit button if all conditions are met
        binding.submitButton.isEnabled = isFormValid
    }

    private fun uploadData() {
        val brandName = binding.formInputBrandName.text.toString().trim()
        val productName = binding.formInputProductName.text.toString().trim()
        val description = binding.formInputDescription.text.toString().trim()
        uploadPhotoToStorage(brandName, productName, description)
    }

    private fun uploadPhotoToStorage(brandName: String, productName: String, description: String) {
        val storageRef = FirebaseStorage.getInstance().reference
        val filePath = "images/${UUID.randomUUID()}.jpg"
        val imageRef = storageRef.child(filePath)

        imageUri?.let { uri ->
            imageRef.putFile(uri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        saveProductToFirebase(brandName, productName, description, downloadUri.toString())
                    }.addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to get download URL", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveProductToFirebase(brandName: String, productName: String, description: String, imageUrl: String) {
        val productId = FirebaseFirestore.getInstance().collection("products").document().id
        val petType = intent.getStringExtra(PET_TYPE_KEY) ?: ""
        val category = intent.getStringExtra(CATEGORY_KEY) ?: ""

        val product = Product(productId, petType, category, brandName, productName, description, imageUrl, averageRating = 0.0, reviewCount = 0, percentageOfUsers = 0)

        FirebaseFirestore.getInstance().collection("products").document(productId)
            .set(product)
            .addOnSuccessListener {
                // Start ProductDetailActivity with product details
                val intent = Intent(this, ProductDetailActivity::class.java).apply {
                    putExtra("productId", productId)
                    putExtra("brandName", brandName)
                    putExtra("productName", productName)
                    putExtra("description", description)
                    putExtra("imageUrl", imageUrl)
                    putExtra("petType", petType)
                    putExtra("category", category)
                    putExtra("fromFillProductInfo", true)
                }
                startActivity(intent)
                Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to add product", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val TAG = "FillProductInformationActivity"
        private const val PICK_IMAGE_REQUEST = 1
    }


}

