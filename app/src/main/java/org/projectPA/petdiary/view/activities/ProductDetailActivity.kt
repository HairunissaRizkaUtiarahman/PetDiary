package org.projectPA.petdiary.view.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import android.view.View
import org.projectPA.petdiary.databinding.ActivityProductDetailBinding
import org.projectPA.petdiary.model.Product
import org.projectPA.petdiary.model.Review
import org.projectPA.petdiary.view.adapters.ReviewAdapter
import org.projectPA.petdiary.viewmodel.ProductDetailViewModel

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private lateinit var productId: String
    private val viewModel: ProductDetailViewModel by viewModels()
    private lateinit var reviewAdapter: ReviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productId = intent.getStringExtra("productId") ?: ""

        if (productId.isEmpty()) {
            Log.e("ProductDetailActivity", "Product ID is missing!")
            Toast.makeText(this, "Product ID is missing!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupRecyclerView()
        observeViewModel()

        viewModel.fetchProductDetails(productId)
    }

    private fun observeViewModel() {
        viewModel.product.observe(this, Observer { product ->
            if (product != null) {
                displayProductDetails(product)
                viewModel.fetchReviews(productId) // Fetch reviews only after product details are loaded
            } else {
                Toast.makeText(this, "Failed to load product details", Toast.LENGTH_SHORT).show()
                finish()
            }
        })

        viewModel.reviews.observe(this, Observer { reviews ->
            reviewAdapter.updateData(reviews)
            updateReviewVisibility(reviews)
        })

        viewModel.errorMessage.observe(this, Observer { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        })
    }

    private fun displayProductDetails(product: Product) {
        binding.productBrandText.text = product.brandName
        binding.productNameText.text = product.productName
        binding.forWhatPetType.text = product.petType
        binding.productCategory.text = product.category
        binding.productDescriptionText.text = product.description
        binding.reviewAverage.text = String.format("%.1f", product.averageRating)
        binding.reviewersCount.text = product.reviewCount.toString()
        binding.percentageOfUser.text = "${product.percentageOfUsers}%"
        Glide.with(this).load(product.imageUrl).into(binding.productPicture)
        binding.ratingBarProduct.rating = product.averageRating.toFloat() // Set rating bar
    }

    private fun setupRecyclerView() {
        binding.listReview.layoutManager = LinearLayoutManager(this)
        reviewAdapter = ReviewAdapter(emptyList())
        binding.listReview.adapter = reviewAdapter
    }

    private fun updateReviewVisibility(reviews: List<Review>) {
        binding.ifThereIsNoReview.visibility = if (reviews.isEmpty()) View.VISIBLE else View.GONE
        binding.listReview.visibility = if (reviews.isEmpty()) View.GONE else View.VISIBLE
        binding.seeMoreReviewLink.visibility = if (reviews.size > 5) View.VISIBLE else View.GONE
    }
}
