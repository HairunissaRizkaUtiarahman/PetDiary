package org.projectPA.petdiary.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.projectPA.petdiary.model.Product

class ProductPageViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private var allProducts: List<Product> = emptyList()
    private var filteredProducts: List<Product> = emptyList()
    private var currentQuery: String? = null

    fun loadProductsFromFirestore(petType: String, category: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val startTime = System.currentTimeMillis()
                val documents = firestore.collection("products")
                    .whereEqualTo("petType", petType)
                    .whereEqualTo("category", category)
                    .get()
                    .await()
                val endTime = System.currentTimeMillis()
                Log.d("ProductPageViewModel", "loadProductsFromFirestore: Time taken: ${endTime - startTime} ms")

                if (documents.isEmpty) {
                    _products.postValue(emptyList())
                } else {
                    val productList = documents.mapNotNull { document ->
                        document.toObject(Product::class.java)
                    }
                    allProducts = productList
                    _products.postValue(productList)
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error loading products: ${e.message}")
            }
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
