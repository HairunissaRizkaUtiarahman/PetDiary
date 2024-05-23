package org.projectPA.petdiary.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
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
        Log.d(TAG, "Loading products for petType: $petType, category: $category")
        firestore.collection("products")
            .whereEqualTo("petType", petType)
            .whereEqualTo("category", category)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.d(TAG, "No products found")
                    _products.value = emptyList()
                } else {
                    val productList = documents.mapNotNull { document ->
                        document.toObject(Product::class.java)
                    }
                    Log.d(TAG, "Products loaded: $productList")
                    allProducts = productList
                    _products.value = productList
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error loading products: ${e.message}")
                _errorMessage.value = "Error loading products: ${e.message}"
            }
    }

    companion object {
        private const val TAG = "ProductPageViewModel"
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
