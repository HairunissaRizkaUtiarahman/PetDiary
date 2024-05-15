package org.projectPA.petdiary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChoosePetCategoryViewModel : ViewModel() {

    private val _selectedPetType = MutableLiveData<String>()
    val selectedPetType: LiveData<String> get() = _selectedPetType

    fun selectPetType(petType: String) {
        _selectedPetType.value = petType
    }
}
