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
import org.projectPA.petdiary.model.Pet
import org.projectPA.petdiary.repository.PetRepository

class PetViewModel(private val petRepository: PetRepository) : ViewModel() {
    private val _pets = MutableLiveData<List<Pet>>()
    private val _pet = MutableLiveData<Pet>()
    private val _isLoading = MutableLiveData<Boolean>()

    val pets: LiveData<List<Pet>>
        get() = _pets

    val pet: LiveData<Pet>
        get() = _pet

    val isLoading: LiveData<Boolean>
        get() = _isLoading

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val myPetRepository =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PetDiaryApplication).petRepository
                PetViewModel(myPetRepository)
            }
        }
    }

    fun uploadData(
        name: String,
        type: String,
        gender: String,
        age: Int,
        desc: String,
        uri: Uri?
    ) = viewModelScope.launch(Dispatchers.IO) {
        _isLoading.postValue(true)
        petRepository.addPet(name, type, gender, age, desc, uri)
        _isLoading.postValue(false)
    }

    fun updateData(
        myPetId: String,
        name: String,
        type: String,
        gender: String,
        age: Int,
        desc: String,
        uri: Uri?
    ) = viewModelScope.launch(Dispatchers.IO) {
        _isLoading.postValue(true)
        petRepository.updatePet(myPetId, name, type, gender, age, desc, uri)
        _isLoading.postValue(false)
    }

    fun loadData() = viewModelScope.launch(Dispatchers.IO) {
        petRepository.getPets().collect { pets ->
            _pets.postValue(pets)
        }
    }

    fun getPet(userId: String) = viewModelScope.launch(Dispatchers.IO) {
        petRepository.getPet(userId)?.let {
            _pet.postValue(it)
        }
    }

    fun setPet(pet: Pet) {
        _pet.value = pet
    }

    fun deleteData(myPetId: String) = viewModelScope.launch(Dispatchers.IO) {
        _isLoading.postValue(true)
        petRepository.deletePet(myPetId)
        _isLoading.postValue(false)
    }
}