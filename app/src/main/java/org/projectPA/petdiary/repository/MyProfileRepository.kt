package org.projectPA.petdiary.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import org.projectPA.petdiary.model.User

private const val LOG_TAG = "MyProfileRepository"

class MyProfileRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storageRef: FirebaseStorage
) {

    suspend fun updateMyProfile(name: String, address: String, bio: String, uri: Uri?) {
        try {
            val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
            val userMap = mutableMapOf(
                "userId" to userId,
                "name" to name,
                "address" to address,
                "bio" to bio
            )

            uri?.let {
                val imageStorageRef = storageRef.getReference("images").child("pictureProfile")
                    .child(System.currentTimeMillis().toString())
                userMap["imageUrl"] = imageStorageRef.putFile(it).await().storage.downloadUrl.await().toString()
            }

            // Update user profile
            db.collection("user").document(userId).update(userMap.toMap()).await()

            // Update user data in review documents
            updateReviewUserData(userId, name, userMap["imageUrl"])
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Failed to update profile data", e)
        } catch (e: StorageException) {
            Log.e(LOG_TAG, "Failed to update profile data", e)
        }
    }

    private suspend fun updateReviewUserData(userId: String, userName: String, userPhotoUrl: String?) {
        try {
            val reviews = db.collection("reviews").whereEqualTo("userId", userId).get().await()
            val batch = db.batch()
            for (review in reviews) {
                val reviewRef = review.reference
                val updateMap = mutableMapOf<String, Any>(
                    "userName" to userName
                )
                if (userPhotoUrl != null) {
                    updateMap["userPhotoUrl"] = userPhotoUrl
                }
                batch.update(reviewRef, updateMap)
            }
            batch.commit().await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Failed to update review data", e)
        }
    }

    fun getMyProfile(): Flow<User?> {
        return flow {
            val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
            val snapshot = db.collection("user").document(userId).get().await()
            val user = snapshot.toObject(User::class.java)?.copy(id = userId)
            emit(user)
        }.catch { e ->
            Log.e(LOG_TAG, "Failed to get user data", e)
            emit(null)
        }
    }
}
