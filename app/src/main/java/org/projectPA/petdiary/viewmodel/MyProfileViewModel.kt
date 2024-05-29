package org.projectPA.petdiary.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.projectPA.petdiary.PetDiaryApplication
import org.projectPA.petdiary.model.User
import org.projectPA.petdiary.repository.MyProfileRepository

class MyProfileViewModel(private val myProfileRepository: MyProfileRepository) : ViewModel() {
    private val _myProfile = MutableLiveData<User?>()
    val myProfile: LiveData<User?>
        get() = _myProfile

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val myProfileRepository =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PetDiaryApplication).myProfileRepository
                MyProfileViewModel(myProfileRepository)
            }
        }
    }

    fun loadData() = viewModelScope.launch {
        withContext(Dispatchers.Main) {
            myProfileRepository.getMyProfile().collect {
                _myProfile.value = it
            }
        }
    }

    fun updateData(name: String, address: String, bio: String, uri: Uri?) = viewModelScope.launch {
        _isLoading.postValue(true)
        myProfileRepository.updateMyProfile(name, address, bio, uri)
        _isLoading.postValue(false)

    }
}