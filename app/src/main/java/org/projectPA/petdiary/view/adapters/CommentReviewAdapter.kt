package org.projectPA.petdiary.view.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ListReviewCommentBinding
import org.projectPA.petdiary.model.CommentReview
import org.projectPA.petdiary.model.User
import org.projectPA.petdiary.relativeTime

class CommentReviewAdapter(
    var comments: List<CommentReview>,
    private val currentUserId: String,
    private val deleteComment: (CommentReview) -> Unit
) : RecyclerView.Adapter<CommentReviewAdapter.CommentViewHolder>() {

    class CommentViewHolder(val binding: ListReviewCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: CommentReview, currentUserId: String) {
            with(binding) {
                val db = FirebaseFirestore.getInstance()
                db.collection("users").document(comment.userId).get()
                    .addOnSuccessListener { document ->
                        val user = document.toObject(User::class.java)
                        if (user != null) {
                            nameTV.text = user.name
                            val userPhotoUrl = user.imageUrl ?: ""

                            if (userPhotoUrl.isEmpty()) {
                                binding.profileImageIV.setImageResource(R.drawable.ic_user)
                            } else {
                                Glide.with(profileImageIV.context).load(user.imageUrl).into(binding.profileImageIV)
                            }
                        } else {
                            Log.e("CommentAdapter", "User not found for userId: ${comment.userId}")
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("CommentAdapter", "Error fetching user details", e)
                    }

                commentTV.text = comment.commentText
                timestampTV.text = comment.timeCommented?.relativeTime() ?: ""
                root.isClickable = comment.userId == currentUserId
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ListReviewCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.bind(comment, currentUserId)
    }

    override fun getItemCount() = comments.size

    fun updateData(commentList: List<CommentReview>) {
        val myComments = commentList.filter { it.userId == currentUserId }
        val otherComments = commentList.filter { it.userId != currentUserId }.sortedByDescending { it.timeCommented }

        comments = myComments + otherComments
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        val comment = comments[position]
        val commentId = comment.id
        if (commentId != null && commentId.isNotEmpty() && comment.userId == currentUserId) {
            Log.d("CommentReviewAdapter", "Attempting to delete comment with ID: $commentId")
            deleteComment(comment)
            comments = comments.toMutableList().apply { removeAt(position) }
            notifyItemRemoved(position)
        } else {
            Log.e("CommentReviewAdapter", "Invalid comment ID or user unauthorized: $commentId")
            notifyItemChanged(position)
        }
    }
}
