package org.projectPA.petdiary.viewmodel

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.projectPA.petdiary.model.Product
import org.projectPA.petdiary.view.activities.ProductDetailActivity
import java.util.*

class FillProductInformationViewModel : ViewModel() {

    private val _imageUri = MutableLiveData<Uri?>()
    val imageUri: LiveData<Uri?> get() = _imageUri

    private val _isFormValid = MutableLiveData<Boolean>()
    val isFormValid: LiveData<Boolean> get() = _isFormValid

    private val _uploadStatus = MutableLiveData<String>()
    val uploadStatus: LiveData<String> get() = _uploadStatus

    fun setImageUri(uri: Uri) {
        _imageUri.value = uri
        validateInputs("", "", "")
    }

    fun validateInputs(brandName: String, productName: String, description: String) {
        _isFormValid.value = brandName.length <= 30 &&
                productName.length in 5..30 &&
                description.length >= 50 &&
                _imageUri.value != null
    }

    fun uploadData(activity: Activity, brandName: String, productName: String, description: String, petType: String, category: String) {
        uploadPhotoToStorage(activity, brandName, productName, description, petType, category)
    }

    private fun uploadPhotoToStorage(activity: Activity, brandName: String, productName: String, description: String, petType: String, category: String) {
        val storageRef = FirebaseStorage.getInstance().reference
        val filePath = "images/${UUID.randomUUID()}.jpg"
        val imageRef = storageRef.child(filePath)

        _imageUri.value?.let { uri ->
            imageRef.putFile(uri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        saveProductToFirebase(activity, brandName, productName, description, downloadUri.toString(), petType, category)
                    }.addOnFailureListener { e ->
                        _uploadStatus.value = "Failed to get download URL"
                    }
                }.addOnFailureListener { e ->
                    _uploadStatus.value = "Failed to upload image"
                }
        }
    }

    private fun saveProductToFirebase(activity: Activity, brandName: String, productName: String, description: String, imageUrl: String, petType: String, category: String) {
        val productId = FirebaseFirestore.getInstance().collection("products").document().id

        val product = Product(productId, petType, category, brandName, productName, description, imageUrl, averageRating = 0.0, reviewCount = 0, percentageOfUsers = 0)

        FirebaseFirestore.getInstance().collection("products").document(productId)
            .set(product)
            .addOnSuccessListener {
                // Start ProductDetailActivity with product details
                val intent = Intent(activity, ProductDetailActivity::class.java).apply {
                    putExtra("productId", productId)
                    putExtra("brandName", brandName)
                    putExtra("productName", productName)
                    putExtra("description", description)
                    putExtra("imageUrl", imageUrl)
                    putExtra("petType", petType)
                    putExtra("category", category)
                    putExtra("fromFillProductInfo", true)
                }
                activity.startActivity(intent)
                _uploadStatus.value = "Product added successfully"
            }
            .addOnFailureListener { e ->
                _uploadStatus.value = "Failed to add product"
            }
    }
}
