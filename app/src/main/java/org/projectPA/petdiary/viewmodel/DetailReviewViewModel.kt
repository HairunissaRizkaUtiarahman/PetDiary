package org.projectPA.petdiary.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import org.projectPA.petdiary.model.Product
import org.projectPA.petdiary.model.Review
import org.projectPA.petdiary.model.User

class DetailReviewViewModel : ViewModel() {

    private val _product = MutableLiveData<Product?>()
    val product: LiveData<Product?> get() = _product

    private val _review = MutableLiveData<Review?>()
    val review: LiveData<Review?> get() = _review

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    private val _commentsCount = MutableLiveData<Int>()
    val commentsCount: LiveData<Int> get() = _commentsCount

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchProductDetails(productId: String) {
        FirebaseFirestore.getInstance().collection("products").document(productId).get()
            .addOnSuccessListener { document ->
                val product = document.toObject(Product::class.java)
                _product.value = product
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Failed to load product details: ${e.message}"
            }
    }

    fun fetchReviewDetails(reviewId: String) {
        FirebaseFirestore.getInstance().collection("reviews").document(reviewId).get()
            .addOnSuccessListener { document ->
                val review = document.toObject(Review::class.java)
                _review.value = review
                review?.let {
                    fetchUserDetails(it.userId)
                }
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Failed to load review details: ${e.message}"
            }
    }

    private fun fetchUserDetails(userId: String) {
        FirebaseFirestore.getInstance().collection("user").document(userId).get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                _user.value = user
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Failed to load user details: ${e.message}"
            }
    }

    fun fetchCommentsCount(reviewId: String) {
        FirebaseFirestore.getInstance().collection("comments")
            .whereEqualTo("reviewId", reviewId)
            .get()
            .addOnSuccessListener { result ->
                _commentsCount.value = result.size()
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Failed to load comments count: ${e.message}"
            }
    }
}
