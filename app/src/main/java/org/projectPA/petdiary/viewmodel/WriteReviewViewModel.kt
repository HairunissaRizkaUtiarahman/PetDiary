package org.projectPA.petdiary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WriteReviewViewModel : ViewModel() {

    private val _reviewText = MutableLiveData<String>()
    val reviewText: LiveData<String> get() = _reviewText

    private val _navigateToRecommend = MutableLiveData<Boolean>()
    val navigateToRecommend: LiveData<Boolean> get() = _navigateToRecommend

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun setReviewText(text: String) {
        _reviewText.value = text
    }

    fun submitReview() {
        if (_reviewText.value.isNullOrBlank()) {
            _errorMessage.value = "Please write a review"
        } else {
            _navigateToRecommend.value = true
        }
    }

    fun doneNavigating() {
        _navigateToRecommend.value = false
    }
}
