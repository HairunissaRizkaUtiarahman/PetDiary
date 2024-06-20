package org.projectPA.petdiary.viewmodel

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
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

    private val _brandName = MutableLiveData<String>()
    private val _productName = MutableLiveData<String>()
    private val _description = MutableLiveData<String>()

    fun setImageUri(uri: Uri) {
        _imageUri.value = uri
        validateInputs(
            _brandName.value ?: "",
            _productName.value ?: "",
            _description.value ?: ""
        )
    }

    fun validateInputs(brandName: String, productName: String, description: String) {
        _brandName.value = brandName
        _productName.value = productName
        _description.value = description

        val isValid = brandName.isNotEmpty() && brandName.length <= 50 &&
                productName.isNotEmpty() && productName.length  >=4 &&
                description.isNotEmpty() && description.length >= 30 &&
                _imageUri.value != null &&
                _productNameError.value == false
        _isFormValid.value = isValid
    }

    fun checkProductNameExists(brandName: String, productName: String) {
        FirebaseFirestore.getInstance().collection("products")
            .whereEqualTo("productNameLower", productName.lowercase())
            .whereEqualTo("brandNameLower", brandName.lowercase())
            .get()
            .addOnSuccessListener { documents ->
                _productNameError.value = !documents.isEmpty
                validateInputs(
                    brandName,
                    productName,
                    _description.value ?: ""
                )
            }
            .addOnFailureListener {
                _productNameError.value = false
                validateInputs(
                    brandName,
                    productName,
                    _description.value ?: ""
                )
            }
    }

    fun uploadData(activity: Activity, brandName: String, productName: String, description: String, petType: String, category: String) {
        val formattedProductName = capitalizeWords(productName)
        FirebaseFirestore.getInstance().collection("products")
            .whereEqualTo("productNameLower", formattedProductName.lowercase())
            .whereEqualTo("brandNameLower", brandName.lowercase())
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    uploadPhotoToStorage(activity, brandName, formattedProductName, description, petType, category)
                } else {
                    _uploadStatus.value = "Product with the same name and brand already exists"
                }
            }
            .addOnFailureListener {
                _uploadStatus.value = "Failed to check product name"
            }
    }

    private fun uploadPhotoToStorage(activity: Activity, brandName: String, productName: String, description: String, petType: String, category: String) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("images").child("pictureProduct").child(System.currentTimeMillis().toString())

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

    private fun saveProductToFirebase(activity: Activity, brandName: String, productName: String, desc: String, imageUrl: String, petType: String, category: String) {
        val productId = FirebaseFirestore.getInstance().collection("products").document().id

        val product = Product(
            id = productId,
            petType = petType,
            category = category,
            brandName = brandName,
            productName = productName,
            desc = desc,
            imageUrl = imageUrl,
            averageRating = 0.0,
            reviewCount = 0,
            percentageOfUsers = 0,
            timeAdded = Timestamp.now(),
            lowercaseProductName = productName.lowercase(),
            lowercaseBrandName = brandName.lowercase()
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

    private fun capitalizeWords(input: String): String {
        return input.split(" ").joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }
    }

    fun createImageUri(context: Context): Uri? {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File(storageDir, "JPEG_${timestamp}_.jpg")
        Log.d("FileProvider", "File path: ${imageFile.absolutePath}")
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", imageFile)
    }
}
