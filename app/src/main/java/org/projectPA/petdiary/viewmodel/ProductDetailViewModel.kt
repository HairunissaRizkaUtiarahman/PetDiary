package org.projectPA.petdiary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import org.projectPA.petdiary.model.Product
import org.projectPA.petdiary.model.Review
import android.util.Log
import com.google.firebase.firestore.FieldValue

class ProductDetailViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _product = MutableLiveData<Product?>()
    val product: LiveData<Product?> get() = _product

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
                    observeReviews(productId)
                } else {
                    _errorMessage.value = "Failed to load product details: Product is null"
                }
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Failed to load product details: ${e.message}"
            }
    }

    private fun observeReviews(productId: String) {
        firestore.collection("reviews")
            .whereEqualTo("productId", productId)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    _errorMessage.value = "Listen failed: ${e.message}"
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val reviews = snapshots.toObjects(Review::class.java)
                    _reviews.value = reviews
                    updateProductReviewCount(productId, reviews.size)
                }
            }
    }

    private fun updateProductReviewCount(productId: String, reviewCount: Int) {
        firestore.collection("products").document(productId)
            .update("reviewCount", reviewCount)
            .addOnSuccessListener {
                // Successfully updated review count
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Failed to update review count: ${e.message}"
            }
    }
}
