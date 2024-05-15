package org.projectPA.petdiary.view.activities


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.projectPA.petdiary.databinding.ActivityFillProductInformationBinding
import org.projectPA.petdiary.model.Product
import java.util.*

class FillProductInformationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFillProductInformationBinding
    private var imageUrl: String? = null
    private val PET_TYPE_KEY = "pet_type"
    private val CATEGORY_KEY = "category"

    @SuppressLint("LongLogTag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFillProductInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(TAG, "onCreate: FillProductInformationActivity started")

        binding.uploadPhotoButton.setOnClickListener {
            uploadPhotoToStorage()
        }

        binding.submitButton.setOnClickListener {
            val brandName = binding.formInputBrandName.text.toString()
            val productName = binding.formInputProductName.text.toString()
            val description = binding.formInputDescription.text.toString()
            if (brandName.isBlank() || productName.isBlank() || description.isBlank()) {
                Toast.makeText(this, "Please fill all fields before submitting.", Toast.LENGTH_LONG).show()
            } else {
                saveProductToFirebase(brandName, productName, description)
            }
        }
    }

    private fun uploadPhotoToStorage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    @SuppressLint("LongLogTag")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data
            val storageRef = FirebaseStorage.getInstance().reference
            val filePath = "images/${UUID.randomUUID()}.jpg"
            val imageRef: StorageReference = storageRef.child(filePath)

            imageUri?.let { uri ->
                imageRef.putFile(uri)
                    .addOnSuccessListener { taskSnapshot ->
                        imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                            imageUrl = downloadUri.toString()
                            Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener { e ->
                            Log.e(TAG, "Failed to get download URL: ${e.message}", e)
                            Toast.makeText(this, "Failed to get download URL", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener { e ->
                        Log.e(TAG, "Upload failed: ${e.message}", e)
                        Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                    }
            } ?: Toast.makeText(this, "Image URI is null", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Image pick cancelled or failed", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("LongLogTag")
    private fun saveProductToFirebase(brandName: String, productName: String, description: String) {
        val productId = FirebaseFirestore.getInstance().collection("products").document().id

        val petType = intent.getStringExtra(PET_TYPE_KEY)
        val category = intent.getStringExtra(CATEGORY_KEY)

        val product = Product(
            id = productId,
            petType = petType ?: "",
            category = category ?: "",
            brandName = brandName,
            productName = productName,
            description = description,
            imageUrl = imageUrl
        )

        FirebaseFirestore.getInstance().collection("products").document(productId)
            .set(product)
            .addOnSuccessListener {
                val intent = Intent(this, ProductDetailActivity::class.java).apply {
                    putExtra("productId", productId)
                    putExtra("brandName", brandName)
                    putExtra("productName", productName)
                    putExtra("description", description)
                    putExtra("imageUrl", imageUrl)
                    putExtra("petType", product.petType)
                    putExtra("category", product.category)
                }
                startActivity(intent)
                Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Product saved and moving to details")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding product", e)
                Toast.makeText(this, "Failed to add product", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val TAG = "FillProductInformationActivity"
        private const val PICK_IMAGE_REQUEST = 1
    }
}
