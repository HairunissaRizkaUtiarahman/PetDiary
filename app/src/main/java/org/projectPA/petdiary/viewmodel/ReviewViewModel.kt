package org.projectPA.petdiary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.projectPA.petdiary.model.Review

class ReviewViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _reviews = MutableLiveData<List<Review>>()
    val reviews: LiveData<List<Review>> get() = _reviews

    fun fetchReviewsByUserId(userId: String) {
        viewModelScope.launch {
            val reviews = try {
                db.collection("reviews")
                    .whereEqualTo("userId", userId)
                    .get().await().map { document ->
                        document.toObject(Review::class.java)
                    }
            } catch (e: Exception) {
                emptyList<Review>()
            }
            _reviews.postValue(reviews)
        }
    }
}
