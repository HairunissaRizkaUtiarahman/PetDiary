package org.projectPA.petdiary.viewmodel

import android.app.Activity
import android.content.Intent
import android.net.Uri
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
                productName.length in 4..30 &&
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
                validateInputsAfterCheck()
            }
            .addOnFailureListener {
                _productNameError.value = false
                validateInputsAfterCheck()
            }
    }

    private fun validateInputsAfterCheck() {
        val brandName = _brandName.value ?: ""
        val productName = _productName.value ?: ""
        val description = _description.value ?: ""
        validateInputs(brandName, productName, description)
    }

    private val _brandName = MutableLiveData<String>()
    val brandName: LiveData<String> get() = _brandName

    private val _productName = MutableLiveData<String>()
    val productName: LiveData<String> get() = _productName

    private val _description = MutableLiveData<String>()
    val description: LiveData<String> get() = _description

    fun updateBrandName(brandName: String) {
        _brandName.value = brandName
        validateInputsAfterCheck()
    }

    fun updateProductName(productName: String) {
        _productName.value = productName
        checkProductNameExists(productName)
    }

    fun updateDescription(description: String) {
        _description.value = description
        validateInputsAfterCheck()
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
                    }.addOnFailureListener {
                        _uploadStatus.value = "Failed to get download URL"
                    }
                }.addOnFailureListener {
                    _uploadStatus.value = "Failed to upload image"
                }
        }
    }

    private fun saveProductToFirebase(activity: Activity, brandName: String, productName: String, description: String, imageUrl: String, petType: String, category: String) {
        val productId = FirebaseFirestore.getInstance().collection("products").document().id

        val product = Product(
            productId, petType, category, brandName, productName, productName.lowercase(), description,
            imageUrl, averageRating = 0.0, reviewCount = 0, percentageOfUsers = 0
        )

        FirebaseFirestore.getInstance().collection("products").document(productId)
            .set(product)
            .addOnSuccessListener {
                _uploadStatus.value = "Product added successfully"
                // Start ProductDetailActivity with product details
                val intent = Intent(activity, ProductDetailActivity::class.java).apply {
                    putExtra("productId", productId)
                    putExtra("sourceActivity", "FillProductInformationActivity")
                }
                activity.startActivity(intent)
            }
            .addOnFailureListener {
                _uploadStatus.value = "Failed to add product"
            }
    }
}
