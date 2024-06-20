package org.projectPA.petdiary.repository

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.google.firebase.storage.FirebaseStorage
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
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storageRef: FirebaseStorage
) {

    // Query Get Review
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

    // Query Get Reviews (My Profile)
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

    // Query Get Reviews (User Profile)
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
}
