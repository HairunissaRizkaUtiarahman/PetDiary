package org.projectPA.petdiary.view.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.projectPA.petdiary.databinding.ItemMyReviewBinding
import org.projectPA.petdiary.model.ReviewWithProduct
import org.projectPA.petdiary.view.activities.DetailReviewActivity
import java.text.SimpleDateFormat
import java.util.Locale

class ReviewWithProductAdapter(
    private var items: List<ReviewWithProduct>,
    private val context: Context
) : RecyclerView.Adapter<ReviewWithProductAdapter.ReviewWithProductViewHolder>() {

    class ReviewWithProductViewHolder(val binding: ItemMyReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ReviewWithProduct, context: Context) {
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

            with(binding) {
                // Set product details
                productName.text = item.product.productName
                brandName.text = item.product.brandName
                reviewDate.text = dateFormat.format(item.review.reviewDate)

                // Set review details
                deskripsiReview.text = item.review.reviewText
                ratingBar4.rating = item.review.rating

                // Load product image
                Glide.with(productImage.context)
                    .load(item.product.imageUrl)
                    .into(productImage)

                // Set click listener to navigate to DetailReviewActivity
                root.setOnClickListener {
                    val intent = Intent(context, DetailReviewActivity::class.java).apply {
                        putExtra("productId", item.product.id)
                        putExtra("reviewId", item.review.id)
                    }
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewWithProductViewHolder {
        val binding = ItemMyReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewWithProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewWithProductViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, context)
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<ReviewWithProduct>) {
        items = newItems
        notifyDataSetChanged()
    }
}
