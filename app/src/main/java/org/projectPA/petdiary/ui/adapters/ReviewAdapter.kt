package org.projectPA.petdiary.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import org.projectPA.petdiary.R
import org.projectPA.petdiary.model.Review

class ReviewAdapter(private var reviews: List<Review>) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userPhoto: CircleImageView = view.findViewById(R.id.user_photo_profile)
        val username: TextView = view.findViewById(R.id.username)
        val reviewDate: TextView = view.findViewById(R.id.review_date)
        val reviewRating: RatingBar = view.findViewById(R.id.ratingBar4)
        val reviewText: TextView = view.findViewById(R.id.deskripsi_review)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.username.text = review.reviewerName
        holder.reviewDate.text = review.date
        holder.reviewRating.rating = review.rating.toFloat()
        holder.reviewText.text = review.text

        // If you have a URL for the user's photo, load it with Glide
        Glide.with(holder.userPhoto.context)
            .load(review.userImageUrl)
            .placeholder(R.drawable.ic_bird)  // Assuming you have a default icon
            .into(holder.userPhoto)
    }

    override fun getItemCount() = reviews.size

    fun updateReviews(newReviews: List<Review>) {
        reviews = newReviews
        notifyDataSetChanged()
    }
}
