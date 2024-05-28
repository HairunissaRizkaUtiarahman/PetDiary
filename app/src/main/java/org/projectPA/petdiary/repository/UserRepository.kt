package org.projectPA.petdiary.repository

import android.util.Log
import com.example.testproject.dataclass.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

private const val LOG_TAG = "UsersRepository"

class UserRepository(
    private val db: FirebaseFirestore
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

    suspend fun searchUser(query: String): List<User> {
        return try {
            val userRef = db.collection("user")
            val queryUserName = userRef.whereGreaterThanOrEqualTo("name", query.uppercase())
                .whereLessThanOrEqualTo("name", query.lowercase() + "\uf8ff")
            queryUserName.get().await().let {
                it.toObjects(User::class.java)
            }
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Fail to set like", e)
            emptyList()
        }
    }
}