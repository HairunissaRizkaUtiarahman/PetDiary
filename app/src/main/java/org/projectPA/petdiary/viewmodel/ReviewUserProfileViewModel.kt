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
import org.projectPA.petdiary.model.Review
import org.projectPA.petdiary.repository.ReviewRepository

class ReviewUserProfileViewModel(private val reviewRepository: ReviewRepository) : ViewModel() {
    private val _reviews = MutableLiveData<List<Review>>()
    private val _review = MutableLiveData<Review>()
    private val _isLoading = MutableLiveData<Boolean>()

    val reviews: LiveData<List<Review>>
        get() = _reviews
    val review: LiveData<Review>
        get() = _review
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val reviewRepository =
                    (this[APPLICATION_KEY] as PetDiaryApplication).reviewRepository
                ReviewUserProfileViewModel(reviewRepository)
            }
        }
    }

    fun loadData(userId: String) = viewModelScope.launch {
        withContext(Dispatchers.Main) {
            reviewRepository.getReviewUserProfile(userId).collect {
                _reviews.value = it
            }
        }
    }

    fun setReview(review: Review) {
        _review.value = review
    }
}