package org.projectPA.petdiary.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await
import org.projectPA.petdiary.model.Product
import org.projectPA.petdiary.model.Review
import org.projectPA.petdiary.model.ReviewWithProduct
import org.projectPA.petdiary.model.User

private const val LOG_TAG = "UserRepository"

class UserRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    suspend fun getUsers(): List<User> {
        return try {
            db.collection("user")
                .get().await().let { querySnapshot ->
                    querySnapshot.documents.mapNotNull {
                        it.toObject(User::class.java)?.copy(id = it.id)
                    }
                }
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Fail to get user", e)
            emptyList()
        }
    }

    suspend fun getRandomUsers(): List<User> {
        return try {
            val userId = auth.currentUser!!.uid
            db.collection("user")
                .whereNotEqualTo("userId", userId) // Exclude the logged-in user
                .limit(10)
                .get().await().let { querySnapshot ->
                    querySnapshot.documents.mapNotNull {
                        it.toObject(User::class.java)?.copy(id = it.id)
                    }
                }
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Fail to get random user", e)
            emptyList()
        }
    }

    suspend fun getUser(userId: String): User? {
        return try {
            db.collection("user")
                .document(userId)
                .get().await()
                .let {
                    it.toObject(User::class.java)?.copy(id = it.id)
                }
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Fail to get user data", e)
            null
        }
    }

    suspend fun searchUser(query: String): List<User> {
        return try {
            db.collection("user")
                .get().await().let { querySnapshot ->
                    querySnapshot.documents.mapNotNull {
                        it.toObject(User::class.java)?.copy(id = it.id)
                    }.filter {
                        it.name?.contains(query, ignoreCase = true) == true
                    }
                }
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Failed to search user", e)
            emptyList()
        }
    }

    suspend fun getUserReviews(userId: String): List<ReviewWithProduct> {
        return try {
            Log.d(LOG_TAG, "Fetching reviews for user ID: $userId")
            val reviewDocuments = db.collection("reviews")
                .whereEqualTo("userId", userId)
                .get().await().documents

            Log.d(LOG_TAG, "Review documents: ${reviewDocuments.size}")

            val reviewsWithProducts = reviewDocuments.mapNotNull { reviewDoc ->
                val review = reviewDoc.toObject(Review::class.java)
                Log.d(LOG_TAG, "Fetched review: $review")
                if (review != null) {
                    val productDoc = db.collection("products").document(review.productId).get().await()
                    val product = productDoc.toObject(Product::class.java)
                    Log.d(LOG_TAG, "Fetched product: $product")
                    if (product != null) {
                        ReviewWithProduct(review, product)
                    } else {
                        Log.d(LOG_TAG, "Product not found for productId: ${review.productId}")
                        null
                    }
                } else {
                    Log.d(LOG_TAG, "Review not found for reviewId: ${reviewDoc.id}")
                    null
                }
            }

            Log.d(LOG_TAG, "Fetched reviews with products: ${reviewsWithProducts.size}")
            reviewsWithProducts
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Failed to get user reviews", e)
            emptyList()
        }
    }
}
