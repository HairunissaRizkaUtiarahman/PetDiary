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
import org.projectPA.petdiary.databinding.ListReviewBinding
import org.projectPA.petdiary.model.Review
import org.projectPA.petdiary.relativeTime

class ReviewMyProfileAdapter(val onClick: (Review, View) -> Unit) :
    ListAdapter<Review, ReviewMyProfileAdapter.ViewHolder>(Companion) {
    private lateinit var context: Context

    companion object : DiffUtil.ItemCallback<Review>() {
        override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem.id == newItem.id
        }
    }

    inner class ViewHolder(private val binding: ListReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(review: Review) = with(binding) {
            brandNameTV.text = review.product?.brandName
            productNameTV.text = review.product?.productName
            reviewDateTV.text = review.reviewDate?.relativeTime() ?: ""
            deskripsiReviewTV.text = review.reviewText
            ratingBar4.rating = review.rating

            Glide.with(productImageIV.context).load(review.product?.imageUrl)
                .placeholder(R.drawable.image_blank).into(productImageIV)

            seeDetailBtn.setOnClickListener {
                onClick(review, it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        val binding = ListReviewBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}