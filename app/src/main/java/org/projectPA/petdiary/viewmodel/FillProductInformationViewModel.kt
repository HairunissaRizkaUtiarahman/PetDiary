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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.projectPA.petdiary.model.Product
import org.projectPA.petdiary.model.Review
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
    private val _review = MutableLiveData<String>()
    private val _rating = MutableLiveData<Float>()
    private val _usage = MutableLiveData<String>()
    private val _recommend = MutableLiveData<Boolean>()

    fun setImageUri(uri: Uri) {
        _imageUri.value = uri
        validateInputs(
            _brandName.value ?: "",
            _productName.value ?: "",
            _description.value ?: "",
            _review.value ?: "",
            _rating.value ?: 0f,
            _usage.value ?: "",
            _recommend.value ?: false
        )
    }

    fun validateInputs(brandName: String, productName: String, description: String, review: String, rating: Float, usage: String, recommend: Boolean) {
        _brandName.value = brandName
        _productName.value = productName
        _description.value = description
        _review.value = review
        _rating.value = rating
        _usage.value = usage
        _recommend.value = recommend

        val isValid = brandName.isNotEmpty() && brandName.length <= 50 &&
                productName.isNotEmpty() && productName.length >= 4 &&
                description.isNotEmpty() && description.length >= 30 &&
                review.isNotEmpty() && review.split(" ").size >= 10 &&
                rating > 0 &&
                usage.isNotEmpty() &&
                _imageUri.value != null &&
                _productNameError.value == false
        _isFormValid.value = isValid
    }

    fun checkProductNameExists(brandName: String, productName: String) {
        FirebaseFirestore.getInstance().collection("products")
            .whereEqualTo("lowercaseProductName", productName.lowercase())
            .get()
            .addOnSuccessListener { documents ->
                _productNameError.value = !documents.isEmpty
                validateInputs(
                    brandName,
                    productName,
                    _description.value ?: "",
                    _review.value ?: "",
                    _rating.value ?: 0f,
                    _usage.value ?: "",
                    _recommend.value ?: false
                )
            }
            .addOnFailureListener {
                _productNameError.value = false
                validateInputs(
                    brandName,
                    productName,
                    _description.value ?: "",
                    _review.value ?: "",
                    _rating.value ?: 0f,
                    _usage.value ?: "",
                    _recommend.value ?: false
                )
            }
    }

    fun uploadData(activity: Activity, brandName: String, productName: String, description: String, petType: String, category: String, review: String, rating: Float, usage: String, recommend: Boolean) {
        val formattedProductName = capitalizeWords(productName)
        FirebaseFirestore.getInstance().collection("products")
            .whereEqualTo("productNameLower", formattedProductName.lowercase())
            .whereEqualTo("brandNameLower", brandName.lowercase())
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    uploadPhotoToStorage(activity, brandName, formattedProductName, description, petType, category, review, rating, usage, recommend)
                } else {
                    _uploadStatus.value = "Product with the same name and brand already exists"
                }
            }
            .addOnFailureListener {
                _uploadStatus.value = "Failed to check product name"
            }
    }

    private fun uploadPhotoToStorage(activity: Activity, brandName: String, productName: String, description: String, petType: String, category: String, review: String, rating: Float, usage: String, recommend: Boolean) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("images").child("pictureProduct").child(System.currentTimeMillis().toString())

        _imageUri.value?.let { uri ->
            imageRef.putFile(uri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        saveProductToFirebase(activity, brandName, productName, description, downloadUri.toString(), petType, category, review, rating, usage, recommend)
                    }.addOnFailureListener { e ->
                        _uploadStatus.value = "Failed to get download URL: ${e.message}"
                    }
                }.addOnFailureListener { e ->
                    _uploadStatus.value = "Failed to upload image: ${e.message}"
                }
        }
    }

    private fun saveProductToFirebase(activity: Activity, brandName: String, productName: String, desc: String, imageUrl: String, petType: String, category: String, review: String, rating: Float, usage: String, recommend: Boolean) {
        val productId = FirebaseFirestore.getInstance().collection("products").document().id

        val product = Product(
            id = productId,
            petType = petType,
            category = category,
            brandName = brandName,
            productName = productName,
            desc = desc,
            imageUrl = imageUrl,
            averageRating = rating.toDouble(),
            reviewCount = 1,
            totalRating = rating.toDouble(),
            percentageOfUsers = if (recommend) 100 else 0,
            timeAdded = Timestamp.now(),
            lowercaseProductName = productName.lowercase(),
            lowercaseBrandName = brandName.lowercase(),
            uploaderName = FirebaseAuth.getInstance().currentUser?.displayName ?: "",
            uploaderReviewDate = Timestamp.now(),
            uploaderReview = review,
            usageUploader = usage,
            ratingUploader = rating.toDouble(),
            recommendUploader = recommend
        )

        FirebaseFirestore.getInstance().collection("products").document(productId)
            .set(product)
            .addOnSuccessListener {
                saveReviewToFirebase(activity, productId, review, rating, usage, recommend)
            }
            .addOnFailureListener { e ->
                _uploadStatus.value = "Failed to add product: ${e.message}"
            }
    }

    private fun saveReviewToFirebase(activity: Activity, productId: String, review: String, rating: Float, usage: String, recommend: Boolean) {
        val reviewId = FirebaseFirestore.getInstance().collection("reviews").document().id

        val reviewData = Review(
            id = reviewId,
            productId = productId,
            userId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
            reviewText = review,
            rating = rating,
            usagePeriod = usage,
            recommend = recommend,
            timeReviewed = Timestamp.now()
        )

        FirebaseFirestore.getInstance().collection("reviews").document(reviewId)
            .set(reviewData)
            .addOnSuccessListener {
                _uploadStatus.value = "Product added successfully"
                Log.d("FillProductInformationViewModel", "Product added successfully with ID: $productId")
                _navigateToProductDetail.value = productId
            }
            .addOnFailureListener { e ->
                _uploadStatus.value = "Failed to add review: ${e.message}"
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
