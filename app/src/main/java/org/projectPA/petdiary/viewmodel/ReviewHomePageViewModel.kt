package org.projectPA.petdiary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import org.projectPA.petdiary.model.Product
import org.projectPA.petdiary.model.Review

class ReviewHomePageViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products

    init {
        loadProducts()
    }

    fun loadProducts() {
        firestore.collection("products")
            .get()
            .addOnSuccessListener { documents ->
                val productList = documents.mapNotNull { it.toObject(Product::class.java) }
                // Sort products by reviewCount in descending order and take top 5
                val topProducts = productList.sortedByDescending { it.reviewCount }.take(5)
                _products.value = topProducts
                observeReviewChanges()
            }
            .addOnFailureListener { exception ->
                // Handle failure (log or update LiveData with error state)
            }
    }

    private fun observeReviewChanges() {
        firestore.collection("reviews")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    for (docChange in snapshots.documentChanges) {
                        val review = docChange.document.toObject(Review::class.java)
                        updateProductReviewCount(review.productId)
                    }
                }
            }
    }

    private fun updateProductReviewCount(productId: String) {
        firestore.collection("reviews")
            .whereEqualTo("productId", productId)
            .get()
            .addOnSuccessListener { reviewDocuments ->
                val reviewCount = reviewDocuments.size()
                firestore.collection("products").document(productId)
                    .update("reviewCount", reviewCount)
                    .addOnSuccessListener {
                        // Successfully updated review count
                        loadProducts() // Reload products to update UI
                    }
                    .addOnFailureListener { e ->
                        // Handle failure
                    }
            }
            .addOnFailureListener { e ->
                // Handle failure
            }
    }
}