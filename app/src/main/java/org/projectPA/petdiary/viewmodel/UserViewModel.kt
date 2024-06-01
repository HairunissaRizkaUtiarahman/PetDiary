package org.projectPA.petdiary.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.projectPA.petdiary.PetDiaryApplication
import org.projectPA.petdiary.model.Product
import org.projectPA.petdiary.model.Review
import org.projectPA.petdiary.model.ReviewWithProduct
import org.projectPA.petdiary.model.User
import org.projectPA.petdiary.repository.UserRepository

private const val LOG_TAG = "UserViewModel"

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _users = MutableLiveData<List<User>>()
    private val _user = MutableLiveData<User>()
    private val _userReviews = MutableLiveData<List<ReviewWithProduct>>()
    val users: LiveData<List<User>>
        get() = _users

    val user: LiveData<User>
        get() = _user

    val userReviews: LiveData<List<ReviewWithProduct>>
        get() = _userReviews

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PetDiaryApplication
                val userRepository = application.userRepository
                UserViewModel(userRepository)
            }
        }
    }

    fun loadRandomUsers() = viewModelScope.launch(Dispatchers.IO) {
        userRepository.getRandomUsers()?.let {
            _users.postValue(it)
        }
    }

    fun getUser(userId: String) = viewModelScope.launch(Dispatchers.IO) {
        userRepository.getUser(userId)?.let {
            _user.postValue(it)
        }
    }

    fun setUser(user: User) {
        _user.value = user
    }

    fun searchUser(query: String) = viewModelScope.launch(Dispatchers.IO) {
        userRepository.searchUser(query.lowercase())?.let {
            _users.postValue(it)
        }
    }

    fun loadUserReviews(userId: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            Log.d(LOG_TAG, "Loading user reviews for user ID: $userId")
            val reviewsWithProducts = userRepository.getUserReviews(userId)
            Log.d(LOG_TAG, "Loaded reviews: ${reviewsWithProducts.size}")
            _userReviews.postValue(reviewsWithProducts)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Error loading user reviews", e)
        }
    }
}