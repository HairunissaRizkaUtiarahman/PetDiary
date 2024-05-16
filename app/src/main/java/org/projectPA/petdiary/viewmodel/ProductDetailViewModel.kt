package org.projectPA.petdiary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import org.projectPA.petdiary.model.Product
import org.projectPA.petdiary.model.Review
import android.util.Log

class ProductDetailViewModel : ViewModel() {

    private val _product = MutableLiveData<Product?>()
    val product: LiveData<Product?> get() = _product

    private val _reviews = MutableLiveData<List<Review>>()
    val reviews: LiveData<List<Review>> get() = _reviews

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchProductDetails(productId: String) {
        Log.d("ProductDetailViewModel", "Fetching product details for ID: $productId")
        FirebaseFirestore.getInstance().collection("products").document(productId).get()
            .addOnSuccessListener { document ->
                val product = document.toObject(Product::class.java)
                if (product != null) {
                    Log.d("ProductDetailViewModel", "Product details fetched successfully")
                    _product.value = product
                } else {
                    val errorMsg = "Failed to load product details: Product is null"
                    Log.e("ProductDetailViewModel", errorMsg)
                    _errorMessage.value = errorMsg
                }
            }
            .addOnFailureListener { e ->
                val errorMsg = "Failed to load product details: ${e.message}"
                Log.e("ProductDetailViewModel", errorMsg)
                _errorMessage.value = errorMsg
            }
    }

    fun fetchReviews(productId: String) {
        Log.d("ProductDetailViewModel", "Fetching reviews for product ID: $productId")
        FirebaseFirestore.getInstance().collection("reviews")
            .whereEqualTo("productId", productId)
            .get()
            .addOnSuccessListener { documents ->
                try {
                    val reviews = documents.mapNotNull { it.toObject(Review::class.java) }
                    reviews.forEach { review ->
                        if (review.userPhotoUrl.isNullOrEmpty()) {
                            review.userPhotoUrl = "default"
                        }
                    }
                    Log.d("ProductDetailViewModel", "Reviews fetched successfully: ${reviews.size}")
                    _reviews.value = reviews
                    updateProductWithReviewData(reviews)
                } catch (e: Exception) {
                    val errorMsg = "Failed to parse reviews: ${e.message}"
                    Log.e("ProductDetailViewModel", errorMsg)
                    _errorMessage.value = errorMsg
                }
            }
            .addOnFailureListener { e ->
                val errorMsg = "Failed to load reviews: ${e.message}"
                Log.e("ProductDetailViewModel", errorMsg)
                _errorMessage.value = errorMsg
            }
    }

    private fun updateProductWithReviewData(reviews: List<Review>) {
        val product = _product.value ?: return

        val totalReviews = reviews.size
        val totalRating = reviews.fold(0.0f) { sum, review -> sum + review.rating }
        val averageRating = if (totalReviews > 0) totalRating / totalReviews else 0.0f

        val recommendCount = reviews.count { it.recommend }
        val percentageOfUsers = if (totalReviews > 0) (recommendCount * 100) / totalReviews else 0

        product.averageRating = averageRating.toDouble()
        product.reviewCount = totalReviews
        product.percentageOfUsers = percentageOfUsers

        _product.value = product
    }
}
