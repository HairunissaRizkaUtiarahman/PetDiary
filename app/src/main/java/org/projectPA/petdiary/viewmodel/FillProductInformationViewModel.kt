package org.projectPA.petdiary.viewmodel

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.projectPA.petdiary.model.Product
import java.io.File
import java.text.SimpleDateFormat
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

    private val _navigateToProductDetail = MutableLiveData<String?>()
    val navigateToProductDetail: LiveData<String?> get() = _navigateToProductDetail

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
        FirebaseFirestore.getInstance().collection("products")
            .whereEqualTo("productNameLower", productName.lowercase())
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    uploadPhotoToStorage(activity, brandName, productName, description, petType, category)
                } else {
                    _uploadStatus.value = "Product Name Already Exist"
                }
            }
            .addOnFailureListener {
                _uploadStatus.value = "Failed to check product name"
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

    private fun saveProductToFirebase(activity: Activity, brandName: String, productName: String, description: String, imageUrl: String, petType: String, category: String) {
        val productId = FirebaseFirestore.getInstance().collection("products").document().id

        val product = Product(
            id = productId,
            petType = petType,
            category = category,
            brandName = brandName,
            productName = productName,
            description = description,
            imageUrl = imageUrl,
            averageRating = 0.0,
            reviewCount = 0,
            percentageOfUsers = 0,
            createdAt = Date(),
            productNameLower = productName.lowercase(),
            brandNameLower = brandName.lowercase()
        )

        FirebaseFirestore.getInstance().collection("products").document(productId)
            .set(product)
            .addOnSuccessListener {
                _uploadStatus.value = "Product added successfully"
                _navigateToProductDetail.value = productId
            }
            .addOnFailureListener { e ->
                _uploadStatus.value = "Failed to add product: ${e.message}"
            }
    }

    fun createImageUri(context: Context): Uri? {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File.createTempFile("JPEG_${timestamp}_", ".jpg", storageDir)
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", imageFile)
    }

    companion object {
        private const val TAG = "FillProductInformationViewModel"
    }
}
