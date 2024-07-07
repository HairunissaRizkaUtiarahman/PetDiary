package org.projectPA.petdiary.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.projectPA.petdiary.model.CommentPost
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ListPostCommentBinding
import org.projectPA.petdiary.relativeTime

class CommentPostUserProfileAdapter(
    private val onDelete: (CommentPost) -> Unit,
    private val currentUserId: String
) :
    ListAdapter<CommentPost, CommentPostUserProfileAdapter.ViewHolder>(Companion) {

    companion object : DiffUtil.ItemCallback<CommentPost>() {
        override fun areContentsTheSame(oldItem: CommentPost, newItem: CommentPost): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(oldItem: CommentPost, newItem: CommentPost): Boolean {
            return oldItem.id == newItem.id
        }
    }

    inner class ViewHolder(private val binding: ListPostCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(commentPost: CommentPost) = with(binding) {
            commentTV.text = commentPost.commentText
            timestampTV.text = commentPost.timeCommented?.relativeTime() ?: ""
            nameTV.text = commentPost.user?.name

            Glide.with(profileImageIV.context).load(commentPost.user?.imageUrl)
                .placeholder(R.drawable.image_blank).into(profileImageIV)

            if (commentPost.userId == currentUserId) {
                deleteBtn.visibility = View.VISIBLE
                deleteBtn.setOnClickListener {
                    onDelete(commentPost)
                }
            } else {
                deleteBtn.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListPostCommentBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}