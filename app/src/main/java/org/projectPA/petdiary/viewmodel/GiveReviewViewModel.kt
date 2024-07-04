package org.projectPA.petdiary.viewmodel

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

    fun submitReview(context: Context, productId: String, sourceActivity: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val reviewRef = firestore.collection("reviews").document()
            review.id = reviewRef.id
            reviewRef.set(review)
                .addOnSuccessListener {
                    Log.d("GiveReviewViewModel", "Review successfully added to Firestore")
                    updateProductStatistics(productId)
                    val intent = Intent(context, ProductDetailActivity::class.java).apply {
                        putExtra("productId", productId)
                        putExtra("sourceActivity", sourceActivity)
                    }
                    context.startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Log.e("GiveReviewViewModel", "Error adding review to Firestore", e)
                }
        } else {
            Log.e("GiveReviewViewModel", "No authenticated user found")
        }
    }

    fun updateProductStatistics(productId: String) {
        val reviewsRef = firestore.collection("reviews").whereEqualTo("productId", productId)
        reviewsRef.get().addOnSuccessListener { documents ->
            val reviews = documents.toObjects(Review::class.java)
            val averageRating = reviews.map { it.rating.toDouble() }.average()
            val reviewCount = reviews.size
            val recommendedCount = reviews.count { it.recommend }
            val percentageOfUsers = if (reviewCount > 0) (recommendedCount * 100 / reviewCount) else 0

            val productRef = firestore.collection("products").document(productId)
            productRef.update(
                mapOf(
                    "averageRating" to averageRating,
                    "reviewCount" to reviewCount,
                    "percentageOfUsers" to percentageOfUsers
                )
            ).addOnSuccessListener {
                Log.d("ProductRepository", "Product stats updated successfully.")
            }.addOnFailureListener { e ->
                Log.e("ProductRepository", "Error updating product stats: ", e)
            }
        }
    }
}




