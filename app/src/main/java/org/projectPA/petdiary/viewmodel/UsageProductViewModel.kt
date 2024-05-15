package org.projectPA.petdiary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UsageProductViewModel : ViewModel() {
    private val _productDetails = MutableLiveData<ProductDetails>()
    val productDetails: LiveData<ProductDetails> get() = _productDetails

    private val _usagePeriod = MutableLiveData<String>()
    val usagePeriod: LiveData<String> get() = _usagePeriod

    data class ProductDetails(
        val brandName: String?,
        val productName: String?,
        val petType: String?,
        val imageUrl: String?
    )

    fun setProductDetails(brandName: String?, productName: String?, petType: String?, imageUrl: String?) {
        _productDetails.value = ProductDetails(brandName, productName, petType, imageUrl)
    }

    fun setUsagePeriod(usagePeriod: String) {
        _usagePeriod.value = usagePeriod
    }
}
