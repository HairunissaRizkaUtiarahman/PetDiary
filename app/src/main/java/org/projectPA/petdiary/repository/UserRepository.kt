package org.projectPA.petdiary.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await
import org.projectPA.petdiary.model.User

private const val LOG_TAG = "UserRepository"

class UserRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    suspend fun getUsers(): List<User> {
        return try {
            val userId = auth.currentUser!!.uid
            db.collection("users")
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

    suspend fun searchUser(query: String): List<User> {
        return try {
            db.collection("users")
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
}
