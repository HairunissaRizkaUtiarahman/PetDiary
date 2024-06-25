package org.projectPA.petdiary.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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

    private val firestore = FirebaseFirestore.getInstance()

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

    fun fetchDataInParallel(productId: String, userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val startTime = System.currentTimeMillis()

                val productDeferred = async { fetchProductDetailsAsync(productId) }
                val reviewsDeferred = async { fetchReviewsAsync(productId) }
                val userReviewDeferred = async { checkIfUserReviewedAsync(productId, userId) }

                productDeferred.await()
                reviewsDeferred.await()
                userReviewDeferred.await()

                val endTime = System.currentTimeMillis()
                Log.d("ProductDetailViewModel", "fetchDataInParallel: Time taken: ${endTime - startTime} ms")
            } catch (e: Exception) {
                _errorMessage.postValue(e.message)
            }
        }
    }

    private suspend fun fetchProductDetailsAsync(productId: String) {
        val startTime = System.currentTimeMillis()
        firestore.collection("products").document(productId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    _product.postValue(document.toObject(Product::class.java))
                } else {
                    _errorMessage.postValue("Product not found")
                }
            }
            .addOnFailureListener { e ->
                _errorMessage.postValue(e.message)
            }
            .await()
        val endTime = System.currentTimeMillis()
        Log.d("ProductDetailViewModel", "fetchProductDetailsAsync: Time taken: ${endTime - startTime} ms")
    }

    private suspend fun fetchReviewsAsync(productId: String) {
        val startTime = System.currentTimeMillis()
        firestore.collection("reviews")
            .whereEqualTo("productId", productId)
            .get()
            .addOnSuccessListener { documents ->
                val reviews = documents.toObjects(Review::class.java)
                _reviews.postValue(reviews)
            }
            .addOnFailureListener { e ->
                _errorMessage.postValue(e.message)
            }
            .await()
        val endTime = System.currentTimeMillis()
        Log.d("ProductDetailViewModel", "fetchReviewsAsync: Time taken: ${endTime - startTime} ms")
    }

    private suspend fun checkIfUserReviewedAsync(productId: String, userId: String) {
        val startTime = System.currentTimeMillis()
        firestore.collection("reviews")
            .whereEqualTo("productId", productId)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                _hasReviewed.postValue(!documents.isEmpty)
            }
            .addOnFailureListener { e ->
                _errorMessage.postValue(e.message)
                _hasReviewed.postValue(false)
            }
            .await()
        val endTime = System.currentTimeMillis()
        Log.d("ProductDetailViewModel", "checkIfUserReviewedAsync: Time taken: ${endTime - startTime} ms")
    }



}
