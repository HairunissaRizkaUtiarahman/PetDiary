package org.projectPA.petdiary.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.projectPA.petdiary.databinding.ListMyReviewBinding
import org.projectPA.petdiary.model.ReviewWithProduct
import java.text.SimpleDateFormat
import java.util.Locale

private const val LOG_TAG = "ReviewWithProductAdapter"

class ReviewWithProductAdapter(
    private var items: List<ReviewWithProduct>,
    private val context: Context
) : RecyclerView.Adapter<ReviewWithProductAdapter.ReviewWithProductViewHolder>() {

    class ReviewWithProductViewHolder(val binding: ListMyReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("LongLogTag")
        fun bind(item: ReviewWithProduct, context: Context) {
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            Log.d(LOG_TAG, "Binding item: $item")

            with(binding) {
                productName.text = item.product.productName
                brandName.text = item.product.brandName
                reviewDate.text = dateFormat.format(item.review.reviewDate)
                deskripsiReview.text = item.review.reviewText
                ratingBar4.rating = item.review.rating

                Glide.with(productImage.context)
                    .load(item.product.imageUrl)
                    .into(productImage)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewWithProductViewHolder {
        val binding = ListMyReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewWithProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewWithProductViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, context)
    }

    override fun getItemCount() = items.size

    @SuppressLint("LongLogTag")
    fun updateData(newItems: List<ReviewWithProduct>) {
        Log.d(LOG_TAG, "Updating adapter data: $newItems")
        items = newItems
        notifyDataSetChanged()
    }
}