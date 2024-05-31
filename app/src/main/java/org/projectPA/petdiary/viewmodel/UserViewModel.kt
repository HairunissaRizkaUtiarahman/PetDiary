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
import org.projectPA.petdiary.PetDiaryApplication
import org.projectPA.petdiary.model.User
import org.projectPA.petdiary.repository.UserRepository

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _users = MutableLiveData<List<User>>()
    private val _user = MutableLiveData<User>()

    val users: LiveData<List<User>>
        get() = _users

    val user: LiveData<User>
        get() = _user

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val usersRepository =
                    (this[APPLICATION_KEY] as PetDiaryApplication).userRepository
                UserViewModel(usersRepository)
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
}
