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

    init {
        loadProducts()
    }

    fun loadProducts() {
        firestore.collection("products")
            .get()
            .addOnSuccessListener { documents ->
                val productList = documents.mapNotNull { it.toObject(Product::class.java) }
                _products.value = productList
            }
            .addOnFailureListener { e ->
                _error.value = "Error loading products: ${e.message}"
            }
    }

    fun searchProducts(query: String) {
        val productsRef = firestore.collection("products")

        val queryProduct = productsRef
            .whereGreaterThanOrEqualTo("productName", query)
            .whereLessThanOrEqualTo("productName", query + "\uf8ff")

        val queryBrand = productsRef
            .whereGreaterThanOrEqualTo("brandName", query)
            .whereLessThanOrEqualTo("brandName", query + "\uf8ff")

        val combinedProducts = mutableSetOf<Product>()

        queryProduct.get().addOnSuccessListener { documents ->
            val productsByName = documents.mapNotNull { it.toObject(Product::class.java) }
            combinedProducts.addAll(productsByName)

            queryBrand.get().addOnSuccessListener { documents ->
                val productsByBrand = documents.mapNotNull { it.toObject(Product::class.java) }
                combinedProducts.addAll(productsByBrand)

                _products.value = combinedProducts.toList()
            }.addOnFailureListener { e ->
                _error.value = "Error searching products by brand: ${e.message}"
            }
        }.addOnFailureListener { e ->
            _error.value = "Error searching products by name: ${e.message}"
        }
    }
}
