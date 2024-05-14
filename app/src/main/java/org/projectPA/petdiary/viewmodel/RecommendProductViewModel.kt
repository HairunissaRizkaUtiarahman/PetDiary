package org.projectPA.petdiary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import org.projectPA.petdiary.model.Review
import java.util.Date

class RecommendProductViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _reviewSubmitted = MutableLiveData<Boolean>()
    val reviewSubmitted: LiveData<Boolean> get() = _reviewSubmitted

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun submitReview(productId: String, rating: Double, usagePeriod: String, reviewText: String, recommend: Boolean) {
        val reviewId = firestore.collection("reviews").document().id
        val review = Review(
            id = reviewId,
            productId = productId,
            rating = rating.toFloat(),
            usagePeriod = usagePeriod,
            reviewText = reviewText,
            recommend = recommend,
            reviewDate = Date(),
            timestamp = System.currentTimeMillis()
        )

        firestore.collection("reviews").document(review.id).set(review)
            .addOnSuccessListener {
                _reviewSubmitted.value = true
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Failed to submit review: ${e.message}"
            }
    }
}
