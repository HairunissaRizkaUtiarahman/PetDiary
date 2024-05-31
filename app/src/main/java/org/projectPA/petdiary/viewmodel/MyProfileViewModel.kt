package org.projectPA.petdiary.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.projectPA.petdiary.PetDiaryApplication
import org.projectPA.petdiary.model.User
import org.projectPA.petdiary.repository.MyProfileRepository

class MyProfileViewModel(private val myProfileRepository: MyProfileRepository) : ViewModel() {
    private val _myProfile = MutableLiveData<User?>()
    val myProfile: LiveData<User?> get() = _myProfile

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val myProfileRepository = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PetDiaryApplication).myProfileRepository
                MyProfileViewModel(myProfileRepository)
            }
        }
    }

    fun loadData() = viewModelScope.launch {
        myProfileRepository.getMyProfile().collect { user ->
            _myProfile.postValue(user)
        }
    }

    fun updateData(name: String, address: String, bio: String, uri: Uri?) = viewModelScope.launch {
        _isLoading.postValue(true)
        myProfileRepository.updateMyProfile(name, address, bio, uri)
        _isLoading.postValue(false)
    }
}
