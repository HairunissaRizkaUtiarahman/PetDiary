package org.projectPA.petdiary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.projectPA.petdiary.PetDiaryApplication
import org.projectPA.petdiary.model.CommentReview
import org.projectPA.petdiary.model.Review
import org.projectPA.petdiary.repository.ReviewRepository

class ReviewUserProfileViewModel(private val reviewRepository: ReviewRepository) : ViewModel() {

    // LiveData to hold list of reviews
    private val _reviews = MutableLiveData<List<Review>>()
    val reviews: LiveData<List<Review>> get() = _reviews

    // LiveData to hold a single review
    private val _review = MutableLiveData<Review>()
    val review: LiveData<Review> get() = _review

    // LiveData to hold list of comments
    private val _commentsReview = MutableLiveData<List<CommentReview>>()
    val commentsReview: LiveData<List<CommentReview>> get() = _commentsReview

    // LiveData to indicate loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // Factory method to create the ViewModel with a repository
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val reviewRepository =
                    (this[APPLICATION_KEY] as PetDiaryApplication).reviewRepository
                ReviewUserProfileViewModel(reviewRepository)
            }
        }
    }

    // Load reviews for a specific user
    fun loadData(userId: String) = viewModelScope.launch {
        withContext(Dispatchers.Main) {
            reviewRepository.getReviewUserProfile(userId).collect {
                _reviews.value = it
            }
        }
    }

    // Set the current review
    fun setReview(review: Review) {
        _review.value = review
    }

    // Add a comment to a review
    fun uploadComment(reviewId: String, text: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            reviewRepository.addCommentReview(reviewId, text)
            // Reload comments after adding a new comment
            loadComment(reviewId)
        }
    }

    // Load comments for a specific review
    fun loadComment(reviewId: String) = viewModelScope.launch {
        withContext(Dispatchers.Main) {
            reviewRepository.getCommentReviews(reviewId).collect {
                _commentsReview.value = it
            }
        }
    }
}
