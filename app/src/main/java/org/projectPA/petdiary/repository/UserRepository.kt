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
            val currentUserUid = auth.currentUser?.uid ?: ""

            db.collection("users")
                .get().await().let { querySnapshot ->
                    querySnapshot.documents.mapNotNull { document ->
                        document.toObject(User::class.java)?.copy(id = document.id)
                    }.filter { user ->
                        user.id != currentUserUid // Exclude current user
                    }
                }
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Failed to get users", e)
            emptyList()
        }
    }

    suspend fun searchUser(query: String): List<User> {
        return try {
            val currentUserUid = auth.currentUser?.uid ?: ""

            db.collection("users")
                .get().await().let { querySnapshot ->
                    querySnapshot.documents.mapNotNull { document ->
                        document.toObject(User::class.java)?.copy(id = document.id)
                    }.filter { user ->
                        user.name?.contains(query, ignoreCase = true) == true && user.id != currentUserUid
                    }
                }
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Failed to search users", e)
            emptyList()
        }
    }
}
