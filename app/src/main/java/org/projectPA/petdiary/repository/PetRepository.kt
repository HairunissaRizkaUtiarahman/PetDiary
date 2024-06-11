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

private const val LOG_TAG = "PetRepository"

class PetRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storageRef: FirebaseStorage
) {

    // Function to add a new pet
    suspend fun addPet(
        name: String,
        type: String,
        gender: String,
        age: Int,
        desc: String,
        uri: Uri?
    ) {
        try {
            // Get current user ID
            val userId = auth.currentUser!!.uid

            // Prepare data to be stored in Firestore
            val petMap = hashMapOf(
                "userId" to userId,
                "name" to name,
                "type" to type,
                "gender" to gender,
                "age" to age,
                "desc" to desc,
                "timestamp" to Timestamp.now(),
                "imageUrl" to ""
            )

            // Upload image if URI is provided
            val imageStorageRef = storageRef.getReference("images").child("picturePet")
                .child(System.currentTimeMillis().toString())

            uri?.let {
                petMap["imageUrl"] =
                    imageStorageRef.putFile(it).await().storage.downloadUrl.await().toString()
            }

            // Add pet data to Firestore
            db.collection("pet").add(petMap).await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Fail to add pet data", e)
        } catch (e: StorageException) {
            Log.e(LOG_TAG, "Fail to upload pet image", e)
        }
    }

    // Function to get a list of pets for the current user
    suspend fun getPets(): Flow<List<Pet>> {
        return try {
            val userId = auth.currentUser!!.uid
            db.collection("pet").whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .snapshots().map { snapshot ->
                    snapshot.map {
                        it.toObject(Pet::class.java).copy(id = it.id)
                    }
                }
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Fail to get pets data", e)
            emptyFlow()
        }
    }

    // Function to get details of a specific pet by user ID
    suspend fun getPet(petId: String): Pet? {
        return try {
            db.collection("pet")
                .document(petId).get().await().let {
                    it.toObject(Pet::class.java)?.copy(id = it.id)
                }
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Fail to get pet data", e)
            null
        }
    }

    // Function to get a list of pets for a specific user by user ID (used for user profile)
    suspend fun getPetsUserProfile(userId: String): Flow<List<Pet>> {
        return try {
            db.collection("pet").whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .snapshots().map { snapshot ->
                    snapshot.map {
                        it.toObject(Pet::class.java).copy(id = it.id)
                    }
                }
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Fail to get pets data", e)
            emptyFlow()
        }
    }

    // Function to update pet details
    suspend fun updatePet(
        petId: String,
        name: String,
        type: String,
        gender: String,
        age: Int,
        desc: String,
        uri: Uri?
    ) {
        try {
            val userId = auth.currentUser!!.uid

            // Prepare updated data
            val petMap = mutableMapOf(
                "userId" to userId,
                "name" to name,
                "type" to type,
                "gender" to gender,
                "age" to age,
                "desc" to desc,
                "timestamp" to Timestamp.now()
            )

            // Upload new image if URI is provided
            val imageStorageRef = storageRef.getReference("images").child("picturePet")
                .child(System.currentTimeMillis().toString())

            uri?.let {
                petMap["imageUrl"] =
                    imageStorageRef.putFile(it).await().storage.downloadUrl.await().toString()
            }

            // Update pet data in Firestore
            db.collection("pet").document(petId).update(petMap.toMap()).await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Fail to update pet data", e)
        } catch (e: StorageException) {
            Log.e(LOG_TAG, "Fail to upload pet image", e)
        }
    }

    // Function to mark a pet as deleted and delete associated image from Firebase Storage
    suspend fun deletePet(petId: String, imageUrl: String?) {
        try {
            // Hapus dokumen pet dari Firestore
            db.collection("pet").document(petId).delete().await()

            // Jika ada URL gambar terkait, hapus gambar dari Firebase Storage
            imageUrl?.let { imageUri ->
                val imageRef = storageRef.getReferenceFromUrl(imageUri)
                imageRef.delete().await()
            }
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Fail to delete pet", e)
        } catch (e: StorageException) {
            Log.e(LOG_TAG, "Fail to delete pet image from Firebase Storage", e)
        }
    }


}
