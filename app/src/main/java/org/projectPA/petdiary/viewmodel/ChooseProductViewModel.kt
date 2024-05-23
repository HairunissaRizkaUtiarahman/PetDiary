package org.projectPA.petdiary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import org.projectPA.petdiary.model.Product

class ChooseProductViewModel : ViewModel() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private var allProducts: List<Product> = emptyList()
    private var filteredProducts: List<Product> = emptyList()
    private var currentQuery: String? = null

    init {
        loadProducts()
    }

    fun loadProducts() {
        firestore.collection("products")
            .get()
            .addOnSuccessListener { documents ->
                val productList = documents.mapNotNull { it.toObject(Product::class.java) }
                allProducts = productList
                _products.value = productList
            }
            .addOnFailureListener { e ->
                _error.value = "Error loading products: ${e.message}"
            }
    }

    fun searchProducts(query: String) {
        currentQuery = query.lowercase()
        val queryParts = currentQuery!!.split(" ")

        filteredProducts = allProducts.filter { product ->
            queryParts.all { part ->
                product.productName.lowercase().contains(part) || product.brandName.lowercase().contains(part)
            }
        }
        _products.value = filteredProducts
    }

    fun clearSearch() {
        currentQuery = null
        filteredProducts = emptyList()
        _products.value = allProducts
    }

    fun sortProducts(sortOption: String) {
        val listToSort = if (currentQuery != null && filteredProducts.isNotEmpty()) filteredProducts else allProducts
        val sortedProducts = when (sortOption) {
            "popular" -> listToSort.sortedByDescending { it.reviewCount }
            "highest_rating" -> listToSort.sortedByDescending { it.averageRating }
            "newest" -> listToSort.sortedByDescending { it.createdAt }
            else -> listToSort
        }


        _products.value = sortedProducts
    }
}
