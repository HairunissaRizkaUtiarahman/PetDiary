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

    // MutableLiveData untuk menyimpan daftar hewan peliharaan
    private val _pets = MutableLiveData<List<Pet>>()
    val pets: LiveData<List<Pet>> get() = _pets

    // MutableLiveData untuk menyimpan data hewan peliharaan tunggal
    private val _pet = MutableLiveData<Pet>()
    val pet: LiveData<Pet> get() = _pet

    // MutableLiveData untuk menyimpan status loading
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val myPetRepository = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PetDiaryApplication).petRepository
                PetViewModel(myPetRepository)
            }
        }
    }

    // Fungsi untuk mengunggah hewan peliharaan baru ke Firestore
    fun uploadPet(
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

    // Fungsi untuk memperbarui data hewan peliharaan di Firestore
    fun updatePet(
        petId: String,
        name: String,
        type: String,
        gender: String,
        age: Int,
        desc: String,
        uri: Uri?
    ) = viewModelScope.launch(Dispatchers.IO) {
        _isLoading.postValue(true)
        petRepository.updatePet(petId, name, type, gender, age, desc, uri)
        _isLoading.postValue(false)
    }

    // Fungsi untuk memuat daftar hewan peliharaan dari Firestore
    fun loadPet() = viewModelScope.launch(Dispatchers.IO) {
        petRepository.getPets().collect { pets ->
            _pets.postValue(pets)
        }
    }

    // Fungsi untuk mengatur data hewan peliharaan ketika dipilih
    fun setPet(pet: Pet) {
        _pet.value = pet
    }

    // Fungsi untuk menghapus hewan peliharaan dari Firestore dan Firebase Storage
    suspend fun deletePet(petId: String, imageUrl: String?): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                petRepository.deletePet(petId, imageUrl)
                _isLoading.postValue(false)
                true
            } catch (e: Exception) {
                _isLoading.postValue(false)
                false
            }
        }
    }
}
