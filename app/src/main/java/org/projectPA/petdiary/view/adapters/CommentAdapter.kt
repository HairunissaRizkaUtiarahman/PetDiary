package org.projectPA.petdiary.view.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ListReviewCommentBinding
import org.projectPA.petdiary.model.CommentsReview
import org.projectPA.petdiary.model.User
import org.projectPA.petdiary.relativeTime
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommentAdapter(
    private var comments: List<CommentsReview>
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    class CommentViewHolder(val binding: ListReviewCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: CommentsReview) {
            with(binding) {
                val db = FirebaseFirestore.getInstance()
                db.collection("user").document(comment.userId).get()
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

                commentTV.text = comment.text
                timestampTV.text = comment.commentDate?.relativeTime() ?: ""
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

    fun updateData(comment: List<CommentsReview>) {
        comments = comment.sortedByDescending { it.commentDate }
        notifyDataSetChanged()
    }
}
