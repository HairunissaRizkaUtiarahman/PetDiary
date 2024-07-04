package org.projectPA.petdiary.viewmodel

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.projectPA.petdiary.model.Product
import org.projectPA.petdiary.model.Review
import org.projectPA.petdiary.model.User
import org.projectPA.petdiary.view.activities.ProductDetailActivity

class GiveReviewViewModel : ViewModel() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _product = MutableLiveData<Product?>()
    val product: MutableLiveData<Product?> get() = _product

    private val _user = MutableLiveData<User?>()
    val user: MutableLiveData<User?> get() = _user

    private val review = Review()

    fun loadProductDetails(productId: String) {
        firestore.collection("products").document(productId)
            .get()
            .addOnSuccessListener { document ->
                val product = document.toObject(Product::class.java)
                _product.value = product
                review.productId = productId
            }
            .addOnFailureListener { e ->
                Log.e("GiveReviewViewModel", "Error loading product details", e)
            }
    }

    fun loadCurrentUser(userId: String) {
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                _user.value = user
                review.userId = userId
                review.user = user
            }
            .addOnFailureListener { e ->
                Log.e("GiveReviewViewModel", "Error loading user details", e)
            }
    }

    fun updateRating(rating: Float) {
        review.rating = rating
    }

    fun updateUsagePeriod(usagePeriod: String) {
        review.usagePeriod = usagePeriod
    }

    fun updateReviewText(reviewText: String) {
        review.reviewText = reviewText
    }

    fun updateRecommendation(recommend: Boolean) {
        review.recommend = recommend
    }

    fun submitReview(context: Context, productId: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val reviewRef = firestore.collection("reviews").document()
            review.id = reviewRef.id
            reviewRef.set(review)
                .addOnSuccessListener {
                    Log.d("GiveReviewViewModel", "Review successfully added to Firestore")
                    viewModelScope.launch {
                        updateProductStatistics(productId)
                        val intent = Intent(context, ProductDetailActivity::class.java).apply {
                            putExtra("productId", productId)
                        }
                        context.startActivity(intent)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("GiveReviewViewModel", "Error adding review to Firestore", e)
                }
        } else {
            Log.e("GiveReviewViewModel", "No authenticated user found")
        }
    }

    private suspend fun updateProductStatistics(productId: String) {
        val reviews = firestore.collection("reviews").whereEqualTo("productId", productId).get().await()
        val recommendedCount = reviews.documents.count { it.getBoolean("recommend") == true }
        val totalRating = reviews.documents.sumOf { it.getDouble("rating") ?: 0.0 }
        val reviewCount = reviews.size()

        val newAverageRating = if (reviewCount > 0) totalRating / reviewCount else 0.0
        val newPercentageOfUsers = if (reviewCount > 0) (recommendedCount * 100 / reviewCount) else 0

        val productRef = firestore.collection("products").document(productId)
        firestore.runTransaction { transaction ->
            transaction.update(productRef, mapOf(
                "reviewCount" to reviewCount,
                "totalRating" to totalRating,
                "averageRating" to newAverageRating,
                "percentageOfUsers" to newPercentageOfUsers
            ))
        }.await()
    }
}
