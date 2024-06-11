package org.projectPA.petdiary.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import org.projectPA.petdiary.model.CommentsReview
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

    private val _comments = MutableLiveData<List<CommentsReview>>()
    val comments: LiveData<List<CommentsReview>> get() = _comments

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
        firestore.collection("user").document(userId).get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                _user.value = user
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Failed to load user details: ${e.message}"
            }
    }

    fun fetchCommentsCount(reviewId: String) {
        firestore.collection("commentsReview")
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
        firestore.collection("commentsReview")
            .whereEqualTo("reviewId", reviewId)
            .orderBy("commentDate", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                val comments = result.mapNotNull {
                    try {
                        it.toObject(CommentsReview::class.java)
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

    fun addComment(comment: CommentsReview) {
        val newCommentRef = firestore.collection("commentsReview").document()
        val newComment = comment.copy(id = newCommentRef.id)

        newCommentRef.set(newComment)
            .addOnSuccessListener {
                _commentAdded.value = true
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Failed to add comment: ${e.message}"
            }
    }
}
