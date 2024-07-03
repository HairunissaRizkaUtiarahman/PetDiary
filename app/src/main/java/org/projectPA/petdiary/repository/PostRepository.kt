package org.projectPA.petdiary.repository

import android.net.Uri
import android.util.Log
import org.projectPA.petdiary.model.CommentPost
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
import org.projectPA.petdiary.model.Like
import org.projectPA.petdiary.model.Post
import org.projectPA.petdiary.model.User

private const val LOG_TAG = "CommunityRepository"

class PostRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storageRef: FirebaseStorage
) {
    suspend fun addPost(desc: String, uri: Uri?) {
        try {
            val userId = auth.currentUser!!.uid
            val postMap = hashMapOf(
                "userId" to userId,
                "desc" to desc,
                "timePosted" to Timestamp.now(),
                "isDeleted" to false,
                "imageUrl" to ""
            )

            val imageStorageRef = storageRef.getReference("images").child("picturePost")
                .child(System.currentTimeMillis().toString())

            uri?.let {
                postMap["imageUrl"] =
                    imageStorageRef.putFile(it).await().storage.downloadUrl.await().toString()
            }

            db.collection("posts").add(postMap).await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Fail to add post data", e)
        } catch (e: StorageException) {
            Log.e(LOG_TAG, "Fail to add post data", e)
        }
    }

    // Query Get Posts
    suspend fun getPosts(): Flow<List<Post>> {
        return try {
            val currentUserID = auth.currentUser!!.uid

            db.collection("posts").whereEqualTo("isDeleted", false)
                .orderBy("timePosted", Query.Direction.DESCENDING)
                .limit(10)
                .snapshots().map { snapshot ->
                    snapshot.map {
                        val userId = it.data["userId"] as String? ?: ""
                        val user = db
                            .collection("users")
                            .document(userId).get()
                            .await().toObject(User::class.java)?.copy(id = userId)

                        val like = db
                            .collection("likes")
                            .document("${currentUserID}_${it.id}").get().await()
                            .toObject(Like::class.java)

                        it.toObject(Post::class.java).copy(id = it.id, user = user, like = like)
                    }
                }
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Fail to get random posts data", e)
            emptyFlow()
        }
    }

    // Query Get Post
    suspend fun getPost(postId: String): Post? {
        return try {
            val post = db.collection("posts")
                .document(postId)
                .get().await()
                .let {
                    val userId = it.get("userId") as String? ?: ""
                    val user = db
                        .collection("users")
                        .document(userId).get().await()
                        .toObject(User::class.java)?.copy(id = userId)
                    val like = db
                        .collection("likes")
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

    // Query Delete Post
    suspend fun deletePost(postId: String) {
        try {
            val petMap = mapOf(
                "isDeleted" to true
            )
            db.collection("posts").document(postId).update(petMap).await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Fail to delete post", e)
        }
    }

    // Query Get Posts (My Profile)
    suspend fun getMyPosts(): Flow<List<Post>> {
        return try {
            val currentUserID = auth.currentUser!!.uid
            val userId = auth.currentUser!!.uid
            db.collection("posts").whereEqualTo("userId", userId).whereEqualTo("isDeleted", false)
                .orderBy("timePosted", Query.Direction.DESCENDING)
                .snapshots().map { snapshot ->
                    snapshot.map {
                        val userId = it.data["userId"] as String? ?: ""
                        val user = db
                            .collection("users")
                            .document(userId).get()
                            .await().toObject(User::class.java)?.copy(id = userId)

                        val like = db
                            .collection("likes")
                            .document("${currentUserID}_${it.id}").get().await()
                            .toObject(Like::class.java)

                        it.toObject(Post::class.java).copy(id = it.id, user = user, like = like)
                    }
                }
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Fail to get post data", e)
            emptyFlow()
        }
    }

    // Query Get Posts (User Profile)
    suspend fun getPostsUserProfile(userId: String): Flow<List<Post>> {
        return try {
            val currentUserID = auth.currentUser!!.uid
            db.collection("posts").whereEqualTo("userId", userId).whereEqualTo("isDeleted", false)
                .orderBy("timePosted", Query.Direction.DESCENDING)
                .snapshots().map { snapshot ->
                    snapshot.map {
                        val userId = it.data["userId"] as String? ?: ""
                        val user = db
                            .collection("users")
                            .document(userId).get()
                            .await().toObject(User::class.java)?.copy(id = userId)

                        val like = db
                            .collection("likes")
                            .document("${currentUserID}_${it.id}").get().await()
                            .toObject(Like::class.java)

                        it.toObject(Post::class.java).copy(id = it.id, user = user, like = like)
                    }
                }
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Fail to get post data", e)
            emptyFlow()
        }
    }

    // Query Add Comment to Post
    suspend fun addCommentPost(commentText: String, postId: String) {
        try {
            val userId = auth.currentUser!!.uid
            val postCommentMap = mapOf(
                "commentText" to commentText,
                "userId" to userId,
                "timeCommented" to Timestamp.now()
            )
            db.collection("posts").document(postId).update("commentCount", FieldValue.increment(1))
                .await()
            db.collection("posts").document(postId).collection("comments").add(postCommentMap).await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Fail to get post data", e)
        }
    }

    // Query Get Comment from Post
    suspend fun getCommentPost(postId: String): Flow<List<CommentPost>> {
        return try {
            db.collection("posts").document(postId).collection("comments")
                .orderBy("timeCommented", Query.Direction.ASCENDING)
                .snapshots().map { snapshot ->
                    snapshot.map {
                        val userId = it.data["userId"] as String? ?: ""
                        val user = db
                            .collection("users")
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

    // Query Set Like
    suspend fun setLike(currentUserID: String, postId: String) {
        try {
            val docId = "${currentUserID}_${postId}"
            val likeRef = db.collection("likes").document(docId)
            val postRef = db.collection("posts").document(postId)
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

    // Query Search Post
    suspend fun searchPost(query: String): List<Post> {
        return try {
            val currentUserID = auth.currentUser!!.uid

            db.collection("posts")
                .get().await().let { querySnapshot ->
                    querySnapshot.documents.mapNotNull {
                        val userId = it.get("userId") as String? ?: ""
                        val user = db
                            .collection("users")
                            .document(userId).get()
                            .await().toObject(User::class.java)?.copy(id = userId)

                        val like = db
                            .collection("likes")
                            .document("${currentUserID}_${it.id}").get().await()
                            .toObject(Like::class.java)

                        it.toObject(Post::class.java)?.copy(id = it.id, user = user, like = like)
                    }.filter {
                        it.desc?.contains(query, ignoreCase = true) == true
                    }
                }
        } catch (e: FirebaseFirestoreException) {
            Log.e(LOG_TAG, "Failed to search post", e)
            emptyList()
        }
    }
}
