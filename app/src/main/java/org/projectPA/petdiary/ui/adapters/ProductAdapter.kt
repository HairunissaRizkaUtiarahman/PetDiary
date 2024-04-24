package org.projectPA.petdiary.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.projectPA.petdiary.R
import org.projectPA.petdiary.model.Product

class ProductAdapter(private val context: Context, private val productList: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.product_image)
        val productName: TextView = itemView.findViewById(R.id.product_name)
        val productBrand: TextView = itemView.findViewById(R.id.product_brand)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        val reviewsAverage: TextView = itemView.findViewById(R.id.reviews_average)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.productImage.setImageResource(product.imageResId)
        holder.productName.text = product.name
        holder.productBrand.text = product.brand
        holder.ratingBar.rating = product.rating
        holder.reviewsAverage.text = "(${product.reviewCount})"
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}
