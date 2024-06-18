package org.projectPA.petdiary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.testproject.dataclass.CommentPost
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

    fun getReview(reviewId: String) = viewModelScope.launch(Dispatchers.IO) {
        reviewRepository.getReview(reviewId)?.let {
            _myReview.postValue(it)
        }
    }

    fun uploadComment(reviewId: String, text: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            reviewRepository.addCommentReview(reviewId, text)
            // Reload comments after adding a new comment
            loadComment(reviewId)
        }
    }

    fun loadComment(reviewId: String) = viewModelScope.launch {
        withContext(Dispatchers.Main) {
            reviewRepository.getCommentReviews(reviewId).collect {
                _CommentsReview.value = it
            }
        }
    }
}
