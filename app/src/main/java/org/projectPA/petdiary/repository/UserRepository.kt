package org.projectPA.petdiary.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import org.projectPA.petdiary.model.User

private const val LOG_TAG = "UsersRepository"

class UserRepository(
    private val db: FirebaseFirestore
) {

    // Query Get Users
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

    // Query Get User
    suspend fun getUser(userId: String): User? {
        return try {
            val user = db.collection("user")
                .document(userId)
                .get().await()
                .let {
                    it.toObject(User::class.java)?.copy(id = it.id)
                }
            user
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, " Fail to get user data")
            null
        }
    }

    // Query Search User
    suspend fun searchUser(query: String): List<User> {
        return try {
            val userRef = db.collection("user")
            val queryUserName = userRef
                .whereGreaterThanOrEqualTo("name", query.uppercase())
                .whereLessThanOrEqualTo("name", query.lowercase() + "\uf8ff")
            val querySnapshot = queryUserName.get().await()
            querySnapshot.documents.mapNotNull {
                it.toObject(User::class.java)?.copy(id = it.id)
            }
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Failed to search user", e)
            emptyList()
        }
    }
}