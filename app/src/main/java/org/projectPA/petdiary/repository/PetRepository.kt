package org.projectPA.petdiary.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import org.projectPA.petdiary.model.Pet

private const val LOG_TAG = "MyPetRepository"
class PetRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storageRef: FirebaseStorage
) {

    // Query Add Pet
    suspend fun addPet(
        name: String,
        type: String,
        gender: String,
        age: Int,
        desc: String,
        uri: Uri?
    ) {
        try {
            val userId = auth.currentUser!!.uid
            val petMap = hashMapOf(
                "userId" to userId,
                "name" to name,
                "type" to type,
                "gender" to gender,
                "age" to age,
                "desc" to desc,
                "timestamp" to Timestamp.now(),
                "isDeleted" to false,
                "imageUrl" to ""
            )

            val imageStorageRef = storageRef.getReference("images").child("picturePet")
                .child(System.currentTimeMillis().toString())

            uri?.let {
                petMap["imageUrl"] =
                    imageStorageRef.putFile(it).await().storage.downloadUrl.await().toString()
            }

            db.collection("pet").add(petMap).await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Fail to add post data", e)
        } catch (e: StorageException) {
            Log.e(LOG_TAG, "Fail to add post data", e)
        }
    }

    // Query Get Pets
    suspend fun getPets(): Flow<List<Pet>> {
        return try {
            val userId = auth.currentUser!!.uid
            db.collection("pet").whereEqualTo("userId", userId)
                .whereEqualTo("isDeleted", false)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .snapshots().map { snapshot ->
                    snapshot.map {
                        it.toObject(Pet::class.java).copy(id = it.id)
                    }
                }
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Fail to get post data", e)
            emptyFlow()
        }
    }

    // Query Get Pet
    suspend fun getPet(userId: String): Pet? {
        return try {
            val pet = db.collection("pet")
                .document(userId).get().await().let {
                    it.toObject(Pet::class.java)?.copy(id = it.id)
                }
            pet
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, " Fail to get pet data")
            null
        }
    }

    // Query Get Pet (User Profile)
    suspend fun getPetsUserProfile(userId: String): Flow<List<Pet>> {
        return try {
            db.collection("pet").whereEqualTo("userId", userId)
                .whereEqualTo("isDeleted", false)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .snapshots().map { snapshot ->
                    snapshot.map {
                        it.toObject(Pet::class.java).copy(id = it.id)
                    }
                }
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Fail to get post data", e)
            emptyFlow()
        }
    }

    // Query Update data Pet
    suspend fun updatePet(
        PetId: String,
        name: String,
        type: String,
        gender: String,
        age: Int,
        desc: String,
        uri: Uri?
    ) {
        try {
            val userId = auth.currentUser!!.uid

            val petMap = mutableMapOf(
                "userId" to userId,
                "name" to name,
                "type" to type,
                "gender" to gender,
                "age" to age,
                "desc" to desc,
                "timestamp" to Timestamp.now(),
                "isDeleted" to false
            )

            val imageStorageRef = storageRef.getReference("images").child("picturePet")
                .child(System.currentTimeMillis().toString())

            uri?.let {
                petMap["imageUrl"] =
                    imageStorageRef.putFile(it).await().storage.downloadUrl.await().toString()
            }
            db.collection("pet").document(PetId).update(petMap.toMap()).await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Fail to update post data", e)
        } catch (e: StorageException) {
            Log.e(LOG_TAG, "Fail to add post data", e)
        }
    }

    // Query Delete Pet
    suspend fun deletePet(PetId: String) {
        try {
            val petMap = mapOf(
                "isDeleted" to true
            )
            db.collection("pet").document(PetId).update(petMap).await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Fail to delete my pet", e)
        }
    }
}