package org.projectPA.petdiary.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ItemReviewBinding
import org.projectPA.petdiary.model.Review
import java.text.SimpleDateFormat
import java.util.Locale

class ReviewAdapter(
    private var reviews: List<Review>
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(review: Review) {
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

            with(binding) {
                username.text = review.userName
                reviewDate.text = dateFormat.format(review.reviewDate)
                deskripsiReview.text = review.reviewText
                ratingBar4.rating = review.rating
                usageProduct.text = review.usagePeriod

                if (review.userPhotoUrl == "default") {
                    userPhotoProfile.setImageResource(R.drawable.ic_users)
                } else {
                    Glide.with(userPhotoProfile.context)
                        .load(review.userPhotoUrl)
                        .into(userPhotoProfile)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.bind(review)
    }

    override fun getItemCount() = reviews.size

    fun updateData(newReviews: List<Review>) {
        reviews = newReviews
        notifyDataSetChanged()
    }
}
