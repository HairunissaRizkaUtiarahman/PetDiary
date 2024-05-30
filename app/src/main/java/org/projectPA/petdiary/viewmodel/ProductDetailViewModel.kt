package org.projectPA.petdiary.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import org.projectPA.petdiary.model.Product
import org.projectPA.petdiary.model.Review
import org.projectPA.petdiary.model.User

class ProductDetailViewModel : ViewModel() {
    private val _product = MutableLiveData<Product>()
    val product: LiveData<Product> get() = _product

    private val _reviews = MutableLiveData<List<Review>>()
    val reviews: LiveData<List<Review>> get() = _reviews

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _hasReviewed = MutableLiveData<Boolean>()
    val hasReviewed: LiveData<Boolean> get() = _hasReviewed

    private val _user = MutableLiveData<User?>()
    val user: MutableLiveData<User?> get() = _user

    private val firestore = FirebaseFirestore.getInstance()

    fun fetchProductDetails(productId: String) {
        firestore.collection("products").document(productId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    _product.value = document.toObject(Product::class.java)
                } else {
                    _errorMessage.value = "Product not found"
                }
            }
            .addOnFailureListener { e ->
                _errorMessage.value = e.message
            }
    }

    fun fetchReviews(productId: String) {
        firestore.collection("reviews")
            .whereEqualTo("productId", productId)
            .get()
            .addOnSuccessListener { documents ->
                val reviews = documents.toObjects(Review::class.java)
                _reviews.value = reviews
            }
            .addOnFailureListener { e ->
                _errorMessage.value = e.message
            }
    }

    fun checkIfUserReviewed(productId: String, userId: String) {
        firestore.collection("reviews")
            .whereEqualTo("productId", productId)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                _hasReviewed.value = !documents.isEmpty
                Log.d("ProductDetailViewModel", "User has reviewed: ${!documents.isEmpty}")
            }
            .addOnFailureListener { e ->
                _errorMessage.value = e.message
                _hasReviewed.value = false
                Log.e("ProductDetailViewModel", "Error checking if user reviewed: ${e.message}")
            }
    }

    // Observe changes to the user data
    fun observeUserData(userId: String) {
        firestore.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _errorMessage.value = error.message
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val updatedUser = snapshot.toObject(User::class.java)
                    _user.value = updatedUser
                    // Update reviews with the new user photo URL
                    updatedUser?.imageUrl?.let { imageUrl ->
                        updateReviewUserPhoto(userId, imageUrl)
                    }
                }
            }
    }

    private fun updateReviewUserPhoto(userId: String, imageUrl: String) {
        firestore.collection("reviews")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val batch = firestore.batch()
                for (document in documents) {
                    val reviewRef = document.reference
                    batch.update(reviewRef, "userPhotoUrl", imageUrl)
                }
                batch.commit()
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Failed to update reviews: ${e.message}"
            }
    }
}
