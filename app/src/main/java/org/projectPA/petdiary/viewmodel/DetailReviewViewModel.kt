package org.projectPA.petdiary.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.projectPA.petdiary.model.CommentReview
import org.projectPA.petdiary.model.Product
import org.projectPA.petdiary.model.Review
import org.projectPA.petdiary.model.User

class DetailReviewViewModel : ViewModel() {

    private val _product = MutableLiveData<Product?>()
    val product: LiveData<Product?> get() = _product

    private val _review = MutableLiveData<Review?>()
    val review: LiveData<Review?> get() = _review

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    private val _commentsCount = MutableLiveData<Int>()
    val commentsCount: LiveData<Int> get() = _commentsCount

    private val _comments = MutableLiveData<List<CommentReview>>()
    val comments: LiveData<List<CommentReview>> get() = _comments

    private val _commentAdded = MutableLiveData<Boolean>()
    val commentAdded: LiveData<Boolean> get() = _commentAdded

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    fun fetchProductDetails(productId: String) {
        firestore.collection("products").document(productId).get()
            .addOnSuccessListener { document ->
                val product = document.toObject(Product::class.java)
                _product.value = product
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Failed to load product details: ${e.message}"
            }
    }

    fun fetchReviewDetails(reviewId: String) {
        firestore.collection("reviews").document(reviewId).get()
            .addOnSuccessListener { document ->
                val review = document.toObject(Review::class.java)
                _review.value = review
                review?.userId?.let { fetchUserDetails(it) }
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Failed to load review details: ${e.message}"
            }
    }

    fun fetchUserDetails(userId: String) {
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                _user.value = user
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Failed to load user details: ${e.message}"
            }
    }

    fun fetchCommentsCount(reviewId: String) {
        firestore.collection("commentReviews")
            .whereEqualTo("reviewId", reviewId)
            .get()
            .addOnSuccessListener { result ->
                _commentsCount.value = result.size()
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Failed to load comments count: ${e.message}"
            }
    }

    fun fetchCommentsForReview(reviewId: String) {
        firestore.collection("commentReviews")
            .whereEqualTo("reviewId", reviewId)
            .orderBy("timeCommented", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                val comments = result.mapNotNull {
                    try {
                        it.toObject(CommentReview::class.java)
                    } catch (e: Exception) {
                        Log.e("DetailReviewViewModel", "Error parsing comment: ${it.id}", e)
                        null
                    }
                }
                _comments.value = comments
                Log.d("DetailReviewViewModel", "Fetched ${comments.size} comments")
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Failed to load comments: ${e.message}"
                Log.e("DetailReviewViewModel", "Error fetching comments", e)
            }
    }


    fun addComment(comment: CommentReview) {
        val newCommentRef = firestore.collection("commentReviews").document()
        val newComment = comment.copy(id = newCommentRef.id)
        Log.d("DetailReviewViewModel", "Generated ID: ${newCommentRef.id}")

        newCommentRef.set(newComment)
            .addOnSuccessListener {
                Log.d("DetailReviewViewModel", "Comment added with ID: ${newComment.id}")
                _commentAdded.value = true
            }
            .addOnFailureListener { e ->
                Log.e("DetailReviewViewModel", "Failed to add comment: ${e.message}")
                _errorMessage.value = "Failed to add comment: ${e.message}"
            }
    }



    fun deleteComment(comment: CommentReview) = viewModelScope.launch {
        try {
            comment.id?.let { id ->
                if (id.isNotEmpty()) {
                    Log.d("DetailReviewViewModel", "Deleting comment with ID: $id")
                    firestore.collection("commentReviews").document(id).delete().await()
                    Log.d("DetailReviewViewModel", "Comment deleted successfully: $id")
                    _commentAdded.postValue(true)
                } else {
                    Log.e("DetailReviewViewModel", "Invalid comment ID: $id")
                    _errorMessage.postValue("Invalid comment ID")
                }
            } ?: run {
                Log.e("DetailReviewViewModel", "Comment ID is null")
                _errorMessage.postValue("Comment ID is null")
            }
        } catch (e: Exception) {
            Log.e("DetailReviewViewModel", "Failed to delete comment: ${comment.id}", e)
            _errorMessage.postValue("Failed to delete comment: ${e.message}")
        }
    }

}
