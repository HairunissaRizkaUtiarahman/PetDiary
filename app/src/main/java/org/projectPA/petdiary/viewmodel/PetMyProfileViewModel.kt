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
import org.projectPA.petdiary.model.Pet
import org.projectPA.petdiary.repository.PetRepository

class PetMyProfileViewModel(private val petRepository: PetRepository) : ViewModel() {
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
                val petRepository =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PetDiaryApplication).petRepository
                PetMyProfileViewModel(petRepository)
            }
        }
    }

    fun loadData() = viewModelScope.launch(Dispatchers.IO) {
        petRepository.getPets().collect { pets ->
            _pets.postValue(pets)
        }
    }

    fun setPet(pet: Pet) {
        _pet.value = pet
    }

}