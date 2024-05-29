package org.projectPA.petdiary.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ListPostBinding
import org.projectPA.petdiary.model.Post
import org.projectPA.petdiary.relativeTime

class PostMyProfileAdapter(
    val onClick: (Post, View) -> Unit,
    val onLike: (Post) -> Unit,
    val onDelete: (Post) -> Unit
) :
    ListAdapter<Post, PostMyProfileAdapter.ViewHolder>(Companion) {
    private lateinit var context: Context

    companion object : DiffUtil.ItemCallback<Post>() {
        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }
    }

    inner class ViewHolder(private val binding: ListPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) = with(binding) {
            descTV.text = post.desc
            nameTV.text = post.user?.name
            timePostTV.text = post.timestamp?.relativeTime() ?: ""

            Glide.with(profileImageIV.context).load(post.user?.imageUrl)
                .placeholder(R.drawable.image_blank).into(profileImageIV)

            if (post.imageUrl != "" && post.imageUrl != null) {
                postImageIV.visibility = View.VISIBLE
                Glide.with(postImageIV.context).load(post.imageUrl)
                    .placeholder(R.drawable.image_blank).into(postImageIV)
            }

            likeCountTV.text = context.getString(R.string.like_count, post.likeCount)

            commentCountTV.text = context.getString(R.string.comment_count, post.commentCount)

            deleteBtn.visibility = View.VISIBLE

            if (post.like != null) {
                likeBtn.visibility = View.VISIBLE
                unlikeBtn.visibility = View.GONE
            } else {
                unlikeBtn.visibility = View.VISIBLE
                likeBtn.visibility = View.GONE
            }

            binding.commentBtn.setOnClickListener {
                onClick(post, it)
            }

            unlikeBtn.setOnClickListener {
                onLike(post)
            }

            likeBtn.setOnClickListener {
                onLike(post)
            }

            deleteBtn.setOnClickListener {
                onDelete(post)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inlater = LayoutInflater.from(parent.context)
        context = parent.context
        val binding = ListPostBinding.inflate(inlater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}