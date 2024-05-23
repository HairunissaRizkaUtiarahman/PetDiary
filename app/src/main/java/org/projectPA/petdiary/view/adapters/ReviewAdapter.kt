package org.projectPA.petdiary.view.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ItemReviewBinding
import org.projectPA.petdiary.model.Review
import org.projectPA.petdiary.view.activities.DetailReviewActivity
import java.text.SimpleDateFormat
import java.util.Locale

class ReviewAdapter(
    private var reviews: List<Review>,
    private val context: Context,
    private val productId: String,
    private val productName: String
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(review: Review, context: Context, productId: String, productName: String) {
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

                // Make the entire root view clickable
                root.setOnClickListener {
                    val intent = Intent(context, DetailReviewActivity::class.java).apply {
                        putExtra("productId", productId)
                        putExtra("reviewId", review.id)
                    }
                    context.startActivity(intent)
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
        holder.bind(review, context, productId, productName)
    }

    override fun getItemCount() = reviews.size

    fun updateData(newReviews: List<Review>) {
        reviews = newReviews
        notifyDataSetChanged()
    }
}