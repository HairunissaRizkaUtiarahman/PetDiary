package org.projectPA.petdiary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.projectPA.petdiary.model.Comment

class ReviewCommentViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    private val _comments = MutableLiveData<List<Comment>>()
    val comments: LiveData<List<Comment>> get() = _comments

    private val _commentAdded = MutableLiveData<Boolean>()
    val commentAdded: LiveData<Boolean> get() = _commentAdded

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun addComment(comment: Comment) {
        val newCommentRef = db.collection("comments").document()
        val newComment = comment.copy(id = newCommentRef.id)

        newCommentRef.set(newComment)
            .addOnSuccessListener {
                _commentAdded.value = true
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Failed to add comment: ${e.message}"
            }
    }

    fun fetchCommentsForReview(reviewId: String) {
        db.collection("comments")
            .whereEqualTo("reviewId", reviewId)
            .get()
            .addOnSuccessListener { result ->
                val comments = result.mapNotNull { it.toObject(Comment::class.java) }
                _comments.value = comments
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Failed to load comments: ${e.message}"
            }
    }
}
