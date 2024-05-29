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
        val text = _reviewText.value ?: ""
        val words = text.trim().split("\\s+".toRegex())
        if (words.size < 10) {
            _errorMessage.value = "Please write a review with at least 10 words"
        } else {
            _navigateToRecommend.value = true
        }
    }

    fun doneNavigating() {
        _navigateToRecommend.value = false
    }
}
