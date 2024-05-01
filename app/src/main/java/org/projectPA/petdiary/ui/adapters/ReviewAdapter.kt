package org.projectPA.petdiary.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import org.projectPA.petdiary.R

class ReviewAdapter(private var reviews: List<DocumentSnapshot>) :
    RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNameTextView: TextView = itemView.findViewById(R.id.username)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar4)
        val descriptionTextView: TextView = itemView.findViewById(R.id.deskripsi_review)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]

        // Set user name
        holder.userNameTextView.text = review.getString("userName")

        // Set rating
        val rating = review.getDouble("rating")
        rating?.let {
            holder.ratingBar.rating = it.toFloat()
        }

        // Set review description
        holder.descriptionTextView.text = review.getString("description")
    }

    override fun getItemCount(): Int {
        return reviews.size
    }

    fun updateReviews(reviews: List<DocumentSnapshot>) {
        this.reviews = reviews
        notifyDataSetChanged()
    }
}