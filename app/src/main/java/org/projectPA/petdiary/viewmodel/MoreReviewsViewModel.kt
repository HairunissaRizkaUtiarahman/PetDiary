package org.projectPA.petdiary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import org.projectPA.petdiary.model.Review

class MoreReviewsViewModel : ViewModel() {

    private val _reviews = MutableLiveData<List<Review>>()
    val reviews: LiveData<List<Review>> get() = _reviews

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private var allReviews: List<Review> = emptyList()

    fun fetchAllReviews(productId: String) {
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
                    allReviews = reviews
                    _reviews.value = reviews
                } catch (e: Exception) {
                    _errorMessage.value = "Failed to parse reviews: ${e.message}"
                }
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Failed to load reviews: ${e.message}"
            }
    }

    fun sortReviews(sortOption: String) {
        val sortedReviews = when (sortOption) {
            "newest" -> allReviews.sortedByDescending { it.reviewDate }
            "oldest" -> allReviews.sortedBy { it.reviewDate }
            "highest_rating" -> allReviews.sortedByDescending { it.rating }
            "lowest_rating" -> allReviews.sortedBy { it.rating }
            else -> allReviews
        }
        _reviews.value = sortedReviews
    }
}
