package org.projectPA.petdiary.view.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ItemReviewBinding
import org.projectPA.petdiary.model.Review
import org.projectPA.petdiary.model.User
import org.projectPA.petdiary.relativeTime
import org.projectPA.petdiary.view.activities.DetailReviewActivity

class ReviewAdapter(
    private var reviews: List<Review>,
    private val context: Context,
    private val productId: String
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()

    class ReviewViewHolder(val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(review: Review, context: Context, productId: String, firestore: FirebaseFirestore) {
            with(binding) {
                reviewDate.text = review.timeReviewed?.relativeTime() ?: ""
                deskripsiReview.text = review.reviewText
                ratingBar4.rating = review.rating
                usageProduct.text = review.usagePeriod

                fetchUserData(review.userId.trim(), context, binding, firestore)

                root.setOnClickListener {
                    val intent = Intent(context, DetailReviewActivity::class.java).apply {
                        putExtra("productId", productId)
                        putExtra("reviewId", review.id)
                    }
                    context.startActivity(intent)
                }
            }
        }

        private fun fetchUserData(userId: String, context: Context, binding: ItemReviewBinding, firestore: FirebaseFirestore) {
            val userDocRef = firestore.collection("users").document(userId)
            Log.d("ReviewAdapter", "Fetching user data from: ${userDocRef.path} for userId: $userId")

            userDocRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val user = document.toObject(User::class.java)
                        if (user != null) {
                            Log.d("ReviewAdapter", "User data fetched successfully: ${user.name}")
                            binding.username.text = user.name
                            val userPhotoUrl = user.imageUrl ?: ""

                            if (userPhotoUrl.isEmpty()) {
                                binding.userPhotoProfile.setImageResource(R.drawable.ic_user)
                            } else {
                                Glide.with(context).load(userPhotoUrl).into(binding.userPhotoProfile)
                            }
                        } else {
                            Log.d("ReviewAdapter", "User data is null for userId: $userId")
                        }
                    } else {
                        Log.d("ReviewAdapter", "No document exists for userId: $userId")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("ReviewAdapter", "Error fetching user data", exception)
                    binding.username.text = "Unknown User"
                    binding.userPhotoProfile.setImageResource(R.drawable.ic_user)
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.bind(review, context, productId, firestore)
    }

    override fun getItemCount() = reviews.size

    fun updateData(newReviews: List<Review>) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val myReviews = newReviews.filter { it.userId == currentUserId }
        val otherReviews = newReviews.filter { it.userId != currentUserId }.sortedByDescending { it.timeReviewed }

        reviews = myReviews + otherReviews
        notifyDataSetChanged()
    }
}
