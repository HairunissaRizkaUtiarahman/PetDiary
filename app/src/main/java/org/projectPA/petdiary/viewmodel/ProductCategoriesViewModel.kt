package org.projectPA.petdiary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProductCategoriesViewModel : ViewModel() {

    private val _petType = MutableLiveData<String?>()
    val petType: LiveData<String?> get() = _petType

    private val _category = MutableLiveData<String?>()
    val category: LiveData<String?> get() = _category

    fun setPetType(petType: String?) {
        _petType.value = petType
    }

    fun setCategory(category: String) {
        _category.value = category
    }
}
