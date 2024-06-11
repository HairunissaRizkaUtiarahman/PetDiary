package org.projectPA.petdiary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.projectPA.petdiary.PetDiaryApplication
import org.projectPA.petdiary.model.ReviewWithProduct
import org.projectPA.petdiary.model.User
import org.projectPA.petdiary.repository.UserRepository

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
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PetDiaryApplication
                val userRepository = application.userRepository
                UserViewModel(userRepository)
            }
        }
    }

    fun loadUsers() = viewModelScope.launch(Dispatchers.IO) {
        userRepository.getUsers().let {
            _users.postValue(it)
        }
    }

    fun setUser(user: User) {
        _user.value = user
    }

    fun searchUser(query: String) = viewModelScope.launch(Dispatchers.IO) {
        userRepository.searchUser(query.lowercase()).let {
            _users.postValue(it)
        }
    }
}