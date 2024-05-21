package org.projectPA.petdiary.viewmodel

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
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

    private val _productNameError = MutableLiveData<Boolean>()
    val productNameError: LiveData<Boolean> get() = _productNameError

    fun setImageUri(uri: Uri) {
        _imageUri.value = uri
        validateInputs("", "", "")
    }

    fun validateInputs(brandName: String, productName: String, description: String) {
        _isFormValid.value = brandName.length <= 30 &&
                productName.length in 5..30 &&
                description.length >= 50 &&
                _imageUri.value != null &&
                _productNameError.value == false
    }

    fun checkProductNameExists(productName: String) {
        FirebaseFirestore.getInstance().collection("products")
            .whereEqualTo("productNameLower", productName.lowercase())
            .get()
            .addOnSuccessListener { documents ->
                _productNameError.value = !documents.isEmpty
                validateInputs("", productName, "")
            }
            .addOnFailureListener {
                _productNameError.value = false
            }
    }


    fun uploadData(activity: Activity, brandName: String, productName: String, description: String, petType: String, category: String) {
        checkProductNameExists(productName)
        if (_productNameError.value == false) {
            uploadPhotoToStorage(activity, brandName, productName, description, petType, category)
        } else {
            _uploadStatus.value = "Product Name Already Exist"
        }
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
                        _uploadStatus.value = "Failed to get download URL: ${e.message}"
                    }
                }.addOnFailureListener { e ->
                    _uploadStatus.value = "Failed to upload image: ${e.message}"
                }
        }
    }

    @SuppressLint("LongLogTag")
    private fun saveProductToFirebase(activity: Activity, brandName: String, productName: String, description: String, imageUrl: String, petType: String, category: String) {
        val productId = FirebaseFirestore.getInstance().collection("products").document().id

        val product = Product(
            productId, petType, category, brandName, productName, description,
            imageUrl, averageRating = 0.0, reviewCount = 0, percentageOfUsers = 0
        )

        FirebaseFirestore.getInstance().collection("products").document(productId)
            .set(product)
            .addOnSuccessListener {
                Log.d(TAG, "Product added successfully: $product")
                _uploadStatus.value = "Product added successfully"
                // Start ProductDetailActivity with product details
                val intent = Intent(activity, ProductDetailActivity::class.java).apply {
                    putExtra("productId", productId)
                }
                activity.startActivity(intent)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to add product: ${e.message}")
                _uploadStatus.value = "Failed to add product: ${e.message}"
            }
    }


    companion object {
        private const val TAG = "FillProductInformationViewModel"
    }
}
