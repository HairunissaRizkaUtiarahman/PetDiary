package org.projectPA.petdiary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import org.projectPA.petdiary.model.Product
import org.projectPA.petdiary.model.Review

class ProductDetailViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _product = MutableLiveData<Product>()
    val product: LiveData<Product> get() = _product

    private val _reviews = MutableLiveData<List<Review>>()
    val reviews: LiveData<List<Review>> get() = _reviews

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchProductDetails(productId: String) {
        firestore.collection("products").document(productId).get()
            .addOnSuccessListener { document ->
                val product = document.toObject(Product::class.java)
                if (product != null) {
                    _product.value = product
                    if (product.reviewCount > 0) {
                        fetchReviews(productId)
                    } else {
                        _reviews.value = emptyList()
                    }
                } else {
                    _errorMessage.value = "Failed to load product details"
                }
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Failed to load product details: ${e.message}"
            }
    }

    private fun fetchReviews(productId: String) {
        firestore.collection("reviews")
            .whereEqualTo("productId", productId)
            .get()
            .addOnSuccessListener { documents ->
                val reviews = documents.mapNotNull { it.toObject(Review::class.java) }
                _reviews.value = reviews
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Error loading reviews: ${e.message}"
            }
    }
}
