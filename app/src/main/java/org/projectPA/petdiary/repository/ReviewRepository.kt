package org.projectPA.petdiary.repository

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import org.projectPA.petdiary.model.CommentReview
import org.projectPA.petdiary.model.Product
import org.projectPA.petdiary.model.Review
import org.projectPA.petdiary.model.User

private const val LOG_TAG = "ReviewRepository"

class ReviewRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    suspend fun getReview(reviewId: String): Review? {
        return try {
            val review = db.collection("reviews")
                .document(reviewId).get().await().let {
                    val productId = it.get("productId") as String? ?: ""
                    val product = db.collection("products").document(productId).get().await()
                        .toObject(Product::class.java)?.copy(id = productId)

                    val userId = it.get("userId") as String? ?: ""
                    val user = db
                        .collection("users")
                        .document(userId).get().await()
                        .toObject(User::class.java)?.copy(id = userId)

                    it.toObject(Review::class.java)
                        ?.copy(id = it.id, user = user, product = product)
                }
            review
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Fail to get review data", e)
            null
        }
    }

    suspend fun getReviewsMyProfile(): Flow<List<Review>> {
        return try {
            val userId = auth.currentUser!!.uid
            db.collection("reviews").whereEqualTo("userId", userId)
                .orderBy("timeReviewed", Query.Direction.DESCENDING)
                .snapshots().map { snapshot ->
                    snapshot.map {
                        val productId = it.data["productId"] as String? ?: ""
                        val product =
                            db.collection("products")
                                .document(productId).get().await()
                                .toObject(Product::class.java)?.copy(id = productId)

                        val userId = it.data["userId"] as String? ?: ""
                        val user = db
                            .collection("users")
                            .document(userId).get()
                            .await().toObject(User::class.java)?.copy(id = userId)

                        it.toObject(Review::class.java)
                            .copy(id = it.id, user = user, product = product)
                    }
                }
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Fail to get review data", e)
            emptyFlow()
        }
    }

    suspend fun getReviewUserProfile(userId: String): Flow<List<Review>> {
        return try {
            db.collection("reviews").whereEqualTo("userId", userId)
                .orderBy("timeReviewed", Query.Direction.DESCENDING)
                .snapshots().map { snapshot ->
                    snapshot.map {
                        val productId = it.data["productId"] as String? ?: ""
                        val product = db.collection("products")
                            .document(productId).get().await()
                            .toObject(Product::class.java)?.copy(id = productId)

                        val userId = it.data["userId"] as String? ?: ""
                        val user = db
                            .collection("users")
                            .document(userId).get()
                            .await().toObject(User::class.java)?.copy(id = userId)

                        it.toObject(Review::class.java)
                            .copy(id = it.id, user = user, product = product)
                    }
                }
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Fail to get review data", e)
            emptyFlow()
        }
    }

    suspend fun getCommentReviews(reviewId: String): Flow<List<CommentReview>> {
        return try {
            db.collection("commentReviews").whereEqualTo("reviewId", reviewId)
                .orderBy("timeCommented", Query.Direction.ASCENDING)
                .snapshots().map { snapshot ->
                    snapshot.map {
                        val userId = it.data["userId"] as String? ?: ""
                        val user = db
                            .collection("users")
                            .document(userId).get()
                            .await().toObject(User::class.java)?.copy(id = userId)

                        it.toObject(CommentReview::class.java).copy(id = it.id, user = user)
                    }
                }
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Fail to get comment data", e)
            emptyFlow()
        }
    }

    suspend fun addCommentReview(reviewId: String, text: String) {
        try {
            val userId = auth.currentUser!!.uid
            val commentMap = hashMapOf(
                "id" to "",
                "timeCommented" to Timestamp.now(),
                "reviewId" to reviewId,
                "commentText" to text,
                "userId" to userId
            )
            val newCommentRef = db.collection("commentReviews").document()
            commentMap["id"] = newCommentRef.id

            newCommentRef.set(commentMap).await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Fail to add comment review", e)
        }
    }

    suspend fun deleteReview(reviewId: String) {
        try {
            db.collection("reviews").document(reviewId).delete().await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Failed to delete review", e)
        }
    }

    suspend fun decrementReviewCountAndUpdateRating(productId: String, review: Review) {
        repeat(3) { attempt ->
            try {
                val userId = auth.currentUser!!.uid
                val productRef = db.collection("products").document(productId)
                Log.d(LOG_TAG, "Starting decrementReviewCountAndUpdateRating for productId: $productId and reviewId: ${review.id}")

                val remainingReviewsSnapshot = db.collection("reviews").whereEqualTo("productId", productId).get().await()
                val remainingReviews = remainingReviewsSnapshot.toObjects(Review::class.java).filter { it.id != review.id }

                val newReviewCount = remainingReviews.size
                val newTotalRating = remainingReviews.sumOf { it.rating.toDouble() }
                val newAverageRating = if (newReviewCount > 0) newTotalRating / newReviewCount else 0.0
                val recommendedCount = remainingReviews.count { it.recommend }
                val newPercentageOfUsers = if (newReviewCount > 0) (recommendedCount * 100 / newReviewCount) else 0

                Log.d(LOG_TAG, "New Review Count: $newReviewCount")
                Log.d(LOG_TAG, "New Total Rating: $newTotalRating")
                Log.d(LOG_TAG, "New Average Rating: $newAverageRating")
                Log.d(LOG_TAG, "New Percentage Of Users: $newPercentageOfUsers")

                db.runTransaction { transaction ->
                    transaction.update(productRef, mapOf(
                        "reviewCount" to newReviewCount,
                        "totalRating" to newTotalRating,
                        "averageRating" to newAverageRating,
                        "percentageOfUsers" to newPercentageOfUsers
                    ))
                }.await()

                Log.d(LOG_TAG, "Successfully updated product: $productId after deleting review: ${review.id}")
                updateUserReviewCount(userId)
                return
            } catch (e: FirebaseFirestoreException) {
                Log.e(LOG_TAG, "Failed attempt ${attempt + 1} to decrement review count and update rating", e)
                if (attempt == 2) throw e
            }
        }
    }

    private suspend fun updateUserReviewCount(userId: String) {
        try {
            val userRef = db.collection("users").document(userId)
            db.runTransaction { transaction ->
                transaction.update(userRef, "reviewCount", FieldValue.increment(-1))
            }.await()
            Log.d("GiveReviewViewModel", "User review count updated successfully")
        } catch (e: Exception) {
            Log.e("GiveReviewViewModel", "Error updating user review count", e)
        }
    }

}
