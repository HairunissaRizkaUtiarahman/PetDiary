// RecommendProductViewModel.kt
package org.projectPA.petdiary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import org.projectPA.petdiary.model.Review

class RecommendProductViewModel : ViewModel() {

    private val _reviewSubmitted = MutableLiveData<Boolean>()
    val reviewSubmitted: LiveData<Boolean> get() = _reviewSubmitted

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun submitReview(review: Review) {
        FirebaseFirestore.getInstance().collection("reviews").document(review.id)
            .set(review)
            .addOnSuccessListener {
                _reviewSubmitted.value = true
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Failed to submit review: ${e.message}"
            }
    }
}

