package org.projectPA.petdiary.view.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import org.projectPA.petdiary.databinding.ListReviewCommentBinding
import org.projectPA.petdiary.model.Comment
import org.projectPA.petdiary.model.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommentAdapter(
    private var comments: List<Comment>
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    class CommentViewHolder(val binding: ListReviewCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) {
            with(binding) {
                val db = FirebaseFirestore.getInstance()
                db.collection("users").document(comment.userId).get()
                    .addOnSuccessListener { document ->
                        val user = document.toObject(User::class.java)
                        if (user != null) {
                            username.text = user.name
                            Glide.with(userPhotoProfile.context)
                                .load(user.imageUrl)
                                .into(userPhotoProfile)
                        } else {
                            Log.e("CommentAdapter", "User not found for userId: ${comment.userId}")
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("CommentAdapter", "Error fetching user details", e)
                    }

                deskripsiComment.text = comment.text
                commentDate.text = SimpleDateFormat(
                    "dd MMM yyyy",
                    Locale.getDefault()
                ).format(comment.CommentDate?.toDate() ?: Date())
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

    fun updateData(newComments: List<Comment>) {
        comments = newComments
        notifyDataSetChanged()
    }
}
