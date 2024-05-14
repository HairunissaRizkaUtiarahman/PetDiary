package org.projectPA.petdiary.viewmodel

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

    fun loadProductsFromFirestore(petType: String, category: String) {
        firestore.collection("products")
            .whereEqualTo("petType", petType)
            .whereEqualTo("category", category)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    _products.value = emptyList()
                } else {
                    val productList = documents.mapNotNull { document ->
                        document.toObject(Product::class.java)
                    }
                    _products.value = productList
                }
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Error loading products: ${e.message}"
            }
    }
}
