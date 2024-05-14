package org.projectPA.petdiary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChooseProductCategoryViewModel : ViewModel() {

    private val _petType = MutableLiveData<String>()
    val petType: LiveData<String> get() = _petType

    fun setPetType(type: String) {
        _petType.value = type
    }
}
