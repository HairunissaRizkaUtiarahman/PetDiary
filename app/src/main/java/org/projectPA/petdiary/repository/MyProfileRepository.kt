package org.projectPA.petdiary.repository

import android.net.Uri
import android.util.Log
import com.example.testproject.dataclass.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

private const val LOG_TAG = "MyPetRepository"
class MyProfileRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storageRef: FirebaseStorage
) {

    suspend fun updateMyProfile(name: String, address: String, bio: String, uri: Uri?) {
        try {
            val userId = auth.currentUser!!.uid
            val userMap = mutableMapOf(
                "userId" to userId,
                "name" to name,
                "address" to address,
                "bio" to bio
            )

            val imageStorageRef = storageRef.getReference("images").child("pictureProfile")
                .child(System.currentTimeMillis().toString())

            uri?.let {
                userMap["imageUrl"] =
                    imageStorageRef.putFile(it).await().storage.downloadUrl.await().toString()
            }
            val dbmyprofile = db.collection("user").document(userId).update(userMap.toMap()).await()

            Log.e(LOG_TAG, dbmyprofile.toString())
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Fail to update post data", e)
        } catch (e: StorageException) {
            Log.e(LOG_TAG, "Fail to update post data", e)
        }
    }

    suspend fun getMyProfile(): Flow<User?> {
        return flow {
            val userId =
                auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
            val snapshot = db.collection("user").document(userId).get().await()
            val user = snapshot.toObject(User::class.java)?.copy(id = userId)
            emit(user)
        }.catch { e ->
            Log.e(LOG_TAG, "Fail to get user data", e)
            emit(null)
        }
    }
}