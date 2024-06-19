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
import java.util.Locale

private const val LOG_TAG = "MyProfileRepository"

class MyProfileRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storageRef: FirebaseStorage
) {

    suspend fun updateMyProfile(
        name: String,
        address: String,
        gender: String,
        birthdate: String,
        bio: String,
        uri: Uri?
    ) {
        try {
            val userId =
                auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
            val lowercaseName = name.lowercase(Locale.ROOT)
            val userMap = mutableMapOf(
                "userId" to userId,
                "name" to name,
                "lowercaseName" to lowercaseName,
                "address" to address,
                "gender" to gender,
                "birthdate" to birthdate,
                "bio" to bio
            )

            uri?.let {
                val imageStorageRef = storageRef.getReference("images").child("pictureProfile")
                    .child(System.currentTimeMillis().toString())
                userMap["imageUrl"] =
                    imageStorageRef.putFile(it).await().storage.downloadUrl.await().toString()
            }

            // Update user profile
            db.collection("users").document(userId).update(userMap.toMap()).await()

        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Failed to update profile data", e)
        } catch (e: StorageException) {
            Log.e(LOG_TAG, "Failed to update profile data", e)
        }
    }

    fun getMyProfile(): Flow<User?> {
        return flow {
            val userId =
                auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
            val snapshot = db.collection("users").document(userId).get().await()
            val user = snapshot.toObject(User::class.java)?.copy(id = userId)
            emit(user)
        }.catch { e ->
            Log.e(LOG_TAG, "Failed to get user data", e)
            emit(null)
        }
    }

    fun checkIfNameExists(name: String, callback: (Boolean) -> Unit) {
        val lowercaseName = name.lowercase(Locale.ROOT)
        val userId = auth.currentUser?.uid
        val db = FirebaseFirestore.getInstance()
        db.collection("users").whereEqualTo("lowercaseName", lowercaseName).get()
            .addOnSuccessListener { documents ->
                var nameExists = false
                for (document in documents) {
                    val id = document.id
                    if (id != userId) {
                        // Jika bukan user yang sedang login, maka namanya sudah ada
                        nameExists = true
                        break
                    }
                }
                callback(nameExists)
            }
            .addOnFailureListener { exception ->
                Log.e(LOG_TAG, "Failed to check name: ${exception.message}", exception)
                callback(false)
            }
    }

}
