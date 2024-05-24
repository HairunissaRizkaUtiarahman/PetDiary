package org.projectPA.petdiary.repository

import android.net.Uri
import android.util.Log
import com.example.testproject.dataclass.CommentPost
import com.example.testproject.dataclass.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import org.projectPA.petdiary.model.Post
import org.projectPA.petdiary.model.Like

private const val LOG_TAG = "CommunityRepository"

class PostRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storageRef: FirebaseStorage
) {
    //Add Post
    suspend fun addPosts(desc: String, uri: Uri?) {
        try {
            val userId = auth.currentUser!!.uid
            val postMap = hashMapOf(
                "userId" to userId,
                "desc" to desc,
                "timestamp" to Timestamp.now(),
                "isDeleted" to false,
                "imageUrl" to ""
            )

            val imageStorageRef = storageRef.getReference("images").child("picturePost")
                .child(System.currentTimeMillis().toString())

            uri?.let {
                postMap["imageUrl"] =
                    imageStorageRef.putFile(it).await().storage.downloadUrl.await().toString()
            }

            db.collection("post").add(postMap).await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Fail to add post data", e)
        } catch (e: StorageException) {
            Log.e(LOG_TAG, "Fail to add post data", e)
        }
    }

    // Add Comment to Post
    suspend fun addCommentPosts(comment: String, postId: String) {
        try {
            val userId = auth.currentUser!!.uid
            val postCommentMap = mapOf(
                "comment" to comment,
                "userId" to userId,
                "timestamp" to Timestamp.now()
            )
            db.collection("post").document(postId).update("commentCount", FieldValue.increment(1))
                .await()
            db.collection("post").document(postId).collection("comment").add(postCommentMap).await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Fail to get post data", e)
        }
    }

    // Query Get many Post
    suspend fun getPosts(currentUserID: String): Flow<List<Post>> {
        return try {
            db.collection("post").whereEqualTo("isDeleted", false)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .snapshots().map { snapshot ->
                    snapshot.map {
                        val userId = it.data["userId"] as String? ?: ""
                        val user = db
                            .collection("user")
                            .document(userId).get()
                            .await().toObject(User::class.java)?.copy(id = userId)

                        val like = db
                            .collection("like")
                            .document("${currentUserID}_${it.id}").get().await()
                            .toObject(Like::class.java)

                        it.toObject(Post::class.java)
                            .copy(id = it.id, user = user, like = like)
                    }
                }
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Fail to get post data", e)
            emptyFlow()
        }
    }

    // Query Get Post
    suspend fun getPost(postId: String): Post? {
        return try {
            val post = db.collection("post")
                .document(postId)
                .get().await()
                .let {
                    val userId = it.get("userId") as String? ?: ""
                    val user = db
                        .collection("user")
                        .document(userId).get().await()
                        .toObject(User::class.java)?.copy(id = userId)
                    val like = db
                        .collection("like")
                        .document("${userId}_${it.id}").get().await()
                        .toObject(Like::class.java)

                    it.toObject(Post::class.java)?.copy(id = it.id, user = user, like = like)
                }
            Log.i(LOG_TAG, "Post $post")
            post
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Fail to get post data", e)
            null
        }
    }

    // Query Get Comment from Post
    suspend fun getCommentPost(postId: String): Flow<List<CommentPost>> {
        return try {
            db.collection("post").document(postId).collection("comment")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .snapshots().map { snapshot ->
                    snapshot.map {
                        val userId = it.data["userId"] as String? ?: ""
                        val user = db
                            .collection("user")
                            .document(userId).get()
                            .await().toObject(User::class.java)?.copy(id = userId)

                        it.toObject(CommentPost::class.java).copy(id = it.id, user = user)
                    }
                }
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Fail to get comment data", e)
            emptyFlow()
        }
    }

    // Query Like
    suspend fun setLike(currentUserID: String, postId: String) {
        try {
            val docId = "${currentUserID}_${postId}"
            val likeRef = db.collection("like").document(docId)
            val postRef = db.collection("post").document(postId)
            if (!likeRef.get().await().exists()) {
                postRef.update("likeCount", FieldValue.increment(1)).await()
                likeRef.set(mapOf("id" to docId)).await()
            } else {
                postRef.update("likeCount", FieldValue.increment(-1)).await()
                likeRef.delete().await()
            }
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Fail to set like", e)
        }
    }
}