package org.projectPA.petdiary.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.projectPA.petdiary.R
import org.projectPA.petdiary.model.Product

class ProductAdapter : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productName: TextView = itemView.findViewById(R.id.product_name)
        private val productBrand: TextView = itemView.findViewById(R.id.product_brand)
        private val productImage: ImageView = itemView.findViewById(R.id.product_image1)
        private val productReviewsCount: TextView = itemView.findViewById(R.id.reviews_count)
        private val rating: TextView = itemView.findViewById(R.id.rating)
        fun bind(product: Product) {
            productName.text = product.name
            productBrand.text = product.brand
            productImage.setImageResource(product.imageRes)
            productReviewsCount.text = "(${product.reviewsCount})"
        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.name == newItem.name // Ubah dengan kunci perbandingan yang sesuai
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}
