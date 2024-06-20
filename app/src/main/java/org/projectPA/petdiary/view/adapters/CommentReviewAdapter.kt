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
    private var comments: List<CommentReview>
) : RecyclerView.Adapter<CommentReviewAdapter.CommentViewHolder>() {

    class CommentViewHolder(val binding: ListReviewCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: CommentReview) {
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
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding =
            ListReviewCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.bind(comment)
    }

    override fun getItemCount() = comments.size

    fun updateData(comment: List<CommentReview>) {
        comments = comment.sortedByDescending { it.timeCommented }
        notifyDataSetChanged()
    }
}
