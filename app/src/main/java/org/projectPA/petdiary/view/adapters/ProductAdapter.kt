package org.projectPA.petdiary.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.projectPA.petdiary.databinding.ItemProductBinding
import org.projectPA.petdiary.model.Product

class ProductAdapter(
    private var products: List<Product>,
    private val onProductClicked: (String) -> Unit

) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product, onProductClicked: (String) -> Unit) {
            with(binding) {
                productName.text = product.productName
                productBrand.text = product.brandName
                reviewCount.text = formatReviewCount(product.reviewCount)
                ratingBar.rating = product.averageRating.toFloat()

                Glide.with(uploadProductImage.context)
                    .load(product.imageUrl)
                    .into(uploadProductImage)

                root.setOnClickListener {
                    onProductClicked(product.id ?: "")
                }
            }
        }


        private fun formatReviewCount(count: Int): String {
            return if (count >= 1000) {
                "${count / 1000}k"
            } else {
                "($count)"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product, onProductClicked)
    }

    override fun getItemCount() = products.size

    fun updateData(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }
}
