package org.projectPA.petdiary.viewmodel

import android.net.Uri
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
    // LiveData for list of pets
    private val _pets = MutableLiveData<List<Pet>>()
    val pets: LiveData<List<Pet>> get() = _pets

    // LiveData for a single pet
    private val _pet = MutableLiveData<Pet>()
    val pet: LiveData<Pet> get() = _pet

    // LiveData to indicate loading status
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // Companion object to provide a factory for creating the ViewModel
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val myPetRepository = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PetDiaryApplication).petRepository
                PetViewModel(myPetRepository)
            }
        }
    }

    // Function to upload pet data
    fun uploadPet(
        name: String,
        type: String,
        gender: String,
        age: Int,
        desc: String,
        uri: Uri?
    ) = viewModelScope.launch(Dispatchers.IO) {
        _isLoading.postValue(true) // Indicate loading start
        petRepository.addPet(name, type, gender, age, desc, uri) // Add pet data to repository
        _isLoading.postValue(false) // Indicate loading end
    }

    // Function to update pet data
    fun updatePet(
        petId: String,
        name: String,
        type: String,
        gender: String,
        age: Int,
        desc: String,
        uri: Uri?
    ) = viewModelScope.launch(Dispatchers.IO) {
        _isLoading.postValue(true) // Indicate loading start
        petRepository.updatePet(petId, name, type, gender, age, desc, uri) // Update pet data in repository
        _isLoading.postValue(false) // Indicate loading end
    }

    // Function to load all pets data
    fun loadPet() = viewModelScope.launch(Dispatchers.IO) {
        petRepository.getPets().collect { pets ->
            _pets.postValue(pets) // Post list of pets to LiveData
        }
    }

    // Function to set pet data manually
    fun setPet(pet: Pet) {
        _pet.value = pet
    }

    // Function to delete pet data and return result as Boolean
    suspend fun deletePet(petId: String, imageUrl: String?): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                _isLoading.postValue(true) // Indicate loading start
                petRepository.deletePet(petId, imageUrl) // Delete pet data from repository
                _isLoading.postValue(false) // Indicate loading end
                true // Return success
            } catch (e: Exception) {
                _isLoading.postValue(false) // Indicate loading end
                false // Return failure
            }
        }
    }
}
