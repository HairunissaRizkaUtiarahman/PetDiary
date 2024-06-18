package org.projectPA.petdiary.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ListReviewCommentBinding
import org.projectPA.petdiary.model.CommentReview
import org.projectPA.petdiary.relativeTime

class CommentReviewUserProfileAdapter :
    ListAdapter<CommentReview, CommentReviewUserProfileAdapter.ViewHolder>(Companion) {

    companion object : DiffUtil.ItemCallback<CommentReview>() {
        override fun areContentsTheSame(oldItem: CommentReview, newItem: CommentReview): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(oldItem: CommentReview, newItem: CommentReview): Boolean {
            return oldItem.id == newItem.id
        }
    }

    inner class ViewHolder(private val binding: ListReviewCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(commentReview: CommentReview) = with(binding) {
            commentTV.text = commentReview.text
            timestampTV.text = commentReview.commentDate?.relativeTime() ?: ""
            nameTV.text = commentReview.user?.name

            Glide.with(profileImageIV.context).load(commentReview.user?.imageUrl)
                .placeholder(R.drawable.image_blank).into(profileImageIV)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListReviewCommentBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}