package org.projectPA.petdiary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import org.projectPA.petdiary.model.Product

class ReviewHomePageViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products

    fun loadProducts() {
        firestore.collection("products")
            .get()
            .addOnSuccessListener { documents ->
                val productList = documents.mapNotNull { it.toObject(Product::class.java) }
                // Sort products by reviewCount in descending order and take top 5
                val topProducts = productList.sortedByDescending { it.reviewCount }.take(5)
                _products.value = topProducts
            }
            .addOnFailureListener { exception ->
                // Handle failure (log or update LiveData with error state)
            }
    }
}
