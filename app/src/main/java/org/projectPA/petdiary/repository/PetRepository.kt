package org.projectPA.petdiary.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
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

    // Query Menambahkan Hewan Peliharaan ke Firestore
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
                "timeAdded" to Timestamp.now(),
                "imageUrl" to ""
            )

            val imageStorageRef = storageRef.getReference("images").child("picturePet")
                .child(System.currentTimeMillis().toString())

            uri?.let {
                petMap["imageUrl"] =
                    imageStorageRef.putFile(it).await().storage.downloadUrl.await().toString()
            }

            db.collection("pets").add(petMap).await()
            db.collection("users").document(userId).update("petCount", FieldValue.increment(1))
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Fail to add pet data", e)
        } catch (e: StorageException) {
            Log.e(LOG_TAG, "Fail to upload pet image", e)
        }
    }

    // Query Memperbarui Data Hewan Peliharaan ke Firestore
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

            val petMap = mutableMapOf(
                "userId" to userId,
                "name" to name,
                "type" to type,
                "gender" to gender,
                "age" to age,
                "desc" to desc
            )

            val imageStorageRef = storageRef.getReference("images").child("picturePet")
                .child(System.currentTimeMillis().toString())

            uri?.let {
                petMap["imageUrl"] =
                    imageStorageRef.putFile(it).await().storage.downloadUrl.await().toString()
            }

            db.collection("pets").document(petId).update(petMap.toMap()).await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Fail to update pet data", e)
        } catch (e: StorageException) {
            Log.e(LOG_TAG, "Fail to upload pet image", e)
        }
    }

    // Query Menghapus Hewan Peliharaan dari Firestore
    suspend fun deletePet(petId: String, imageUrl: String?) {
        try {
            val userId = auth.currentUser!!.uid
            db.collection("pets").document(petId).delete().await()
            db.collection("users").document(userId).update("petCount", FieldValue.increment(-1))

            if (!imageUrl.isNullOrEmpty()) {
                val imageRef = storageRef.getReferenceFromUrl(imageUrl)
                imageRef.delete().await()
            }
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Fail to delete pet", e)
        } catch (e: StorageException) {
            Log.e(LOG_TAG, "Fail to delete pet image from Firebase Storage", e)
        }
    }

    // Query Mengambil Data Hewan Peliharaan dari Firestore untuk Manage Pet dan My Profile
    fun getPets(): Flow<List<Pet>> {
        return try {
            val userId = auth.currentUser!!.uid
            db.collection("pets").whereEqualTo("userId", userId)
                .orderBy("timeAdded", Query.Direction.DESCENDING)
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

    // Query Mengambil Data Hewan Peliharaan dari Firestore untuk User Profile
    fun getPetsUserProfile(userId: String): Flow<List<Pet>> {
        return try {
            db.collection("pets").whereEqualTo("userId", userId)
                .orderBy("timeAdded", Query.Direction.DESCENDING)
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
}
