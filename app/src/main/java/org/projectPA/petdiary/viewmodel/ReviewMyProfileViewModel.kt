package org.projectPA.petdiary.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.core.content.PackageManagerCompat.LOG_TAG
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.projectPA.petdiary.PetDiaryApplication
import org.projectPA.petdiary.model.CommentReview
import org.projectPA.petdiary.model.Review
import org.projectPA.petdiary.repository.ReviewRepository

class ReviewMyProfileViewModel(private val reviewRepository: ReviewRepository) : ViewModel() {
    private val _myReviews = MutableLiveData<List<Review>>()
    private val _myReview = MutableLiveData<Review>()

    private val _CommentsReview = MutableLiveData<List<CommentReview>>()
    private val _isLoading = MutableLiveData<Boolean>()

    val myReviews: LiveData<List<Review>>
        get() = _myReviews
    val myReview: LiveData<Review>
        get() = _myReview

    val commentsReview: LiveData<List<CommentReview>>
        get() = _CommentsReview

    private val _commentsCount = MutableLiveData<Int>()

    private val firestore = FirebaseFirestore.getInstance()
    private val _commentAdded = MutableLiveData<Boolean>()

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    val isLoading: LiveData<Boolean>
        get() = _isLoading

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val reviewRepository =
                    (this[APPLICATION_KEY] as PetDiaryApplication).reviewRepository
                ReviewMyProfileViewModel(reviewRepository)
            }
        }
    }

    fun loadData() = viewModelScope.launch {
        withContext(Dispatchers.Main) {
            reviewRepository.getReviewsMyProfile().collect {
                _myReviews.value = it
            }
        }
    }

    fun setReview(review: Review) {
        _myReview.value = review
    }

    fun uploadComment(reviewId: String, text: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            reviewRepository.addCommentReview(reviewId, text)
            loadComment(reviewId)
            fetchCommentsCount(reviewId)
        }
    }

    fun loadComment(reviewId: String) = viewModelScope.launch {
        withContext(Dispatchers.Main) {
            reviewRepository.getCommentReviews(reviewId).collect {
                _CommentsReview.value = it
                _commentsCount.value = it.size
            }
        }
    }

    @SuppressLint("RestrictedApi")
    fun deleteReview(reviewId: String, productId: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            Log.d(LOG_TAG, "Attempting to delete review with ID: $reviewId for product ID: $productId")
            val review = reviewRepository.getReview(reviewId)
            if (review != null) {
                Log.d(LOG_TAG, "Review found. Deleting review with ID: $reviewId")
                reviewRepository.deleteReview(reviewId)
                Log.d(LOG_TAG, "Review deleted. Updating product stats for product ID: $productId")
                reviewRepository.decrementReviewCountAndUpdateRating(productId, review)
                loadData()
            } else {
                Log.e(LOG_TAG, "Review not found for ID: $reviewId")
            }
        }
    }


    fun fetchCommentsCount(reviewId: String) = viewModelScope.launch {
        firestore.collection("commentReviews")
            .whereEqualTo("reviewId", reviewId)
            .get()
            .addOnSuccessListener { result ->
                _commentsCount.value = result.size()
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Failed to load comments count: ${e.message}"
            }
    }
}
