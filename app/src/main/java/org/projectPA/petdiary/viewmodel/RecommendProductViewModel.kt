package org.projectPA.petdiary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import org.projectPA.petdiary.model.Product
import org.projectPA.petdiary.model.Review

class RecommendProductViewModel : ViewModel() {

    private val _reviewSubmitted = MutableLiveData<Boolean>()
    val reviewSubmitted: LiveData<Boolean> get() = _reviewSubmitted

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun submitReview(review: Review) {
        FirebaseFirestore.getInstance().collection("reviews").document(review.id ?: "")
            .set(review)
            .addOnSuccessListener {
                updateProductWithReview(review.productId)
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Failed to submit review: ${e.message}"
            }
    }

    private fun updateProductWithReview(productId: String) {
        val db = FirebaseFirestore.getInstance()
        val reviewsRef = db.collection("reviews").whereEqualTo("productId", productId)
        val productRef = db.collection("products").document(productId)

        reviewsRef.get().addOnSuccessListener { documents ->
            val reviews = documents.toObjects(Review::class.java)
            val totalReviews = reviews.size
            val totalRating = reviews.fold(0.0f) { sum, review -> sum + review.rating }
            val averageRating = if (totalReviews > 0) totalRating / totalReviews else 0.0f

            val recommendCount = reviews.count { it.recommend }
            val percentageOfUsers = if (totalReviews > 0) (recommendCount * 100) / totalReviews else 0

            productRef.update(
                mapOf(
                    "averageRating" to averageRating.toDouble(),
                    "reviewCount" to totalReviews,
                    "percentageOfUsers" to percentageOfUsers
                )
            ).addOnSuccessListener {
                _reviewSubmitted.value = true
            }.addOnFailureListener { e ->
                _errorMessage.value = "Failed to update product: ${e.message}"
            }
        }.addOnFailureListener { e ->
            _errorMessage.value = "Failed to fetch reviews: ${e.message}"
        }
    }
}
