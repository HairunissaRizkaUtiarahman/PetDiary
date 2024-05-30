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

class MyPetViewModel(private val petRepository: PetRepository) : ViewModel() {
    private val _pet = MutableLiveData<List<Pet>>()
    val pets: LiveData<List<Pet>>
        get() = _pet
    var pet: Pet = Pet()
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val myPetRepository =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PetDiaryApplication).petRepository
                MyPetViewModel(myPetRepository)
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
        petRepository.addPets(name, type, gender, age, desc, uri)
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
        petRepository.updatePets(myPetId, name, type, gender, age, desc, uri)
        _isLoading.postValue(false)

    }

    fun loadData() = viewModelScope.launch {
        withContext(Dispatchers.Main) {
            petRepository.getPets().collect {
                _pet.value = it
                Log.d("MyPetViewModel", it.toString())
            }

        }
    }

    fun deleteData(myPetId: String) = viewModelScope.launch(Dispatchers.IO) {
        _isLoading.postValue(true)
        petRepository.deletePets(myPetId)
        _isLoading.postValue(false)
    }

}