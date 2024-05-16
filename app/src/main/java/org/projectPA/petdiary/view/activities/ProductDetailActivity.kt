package org.projectPA.petdiary.view.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
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

        Log.d("ProductDetailActivity", "Product ID: $productId")

        binding.backButton.setOnClickListener {
            val intent = Intent(this, ReviewHomePageActivity::class.java)
            startActivity(intent)
            finish()
        }

        setupRecyclerView()
        observeViewModel()

        viewModel.fetchProductDetails(productId)
    }

    private fun observeViewModel() {
        viewModel.product.observe(this, Observer { product ->
            if (product != null) {
                Log.d("ProductDetailActivity", "Product details loaded successfully")
                displayProductDetails(product)
                viewModel.fetchReviews(productId) // Fetch reviews only after product details are loaded
            } else {
                Log.e("ProductDetailActivity", "Product details are null")
                Toast.makeText(this, "Failed to load product details", Toast.LENGTH_SHORT).show()
                finish()
            }
        })

        viewModel.reviews.observe(this, Observer { reviews ->
            Log.d("ProductDetailActivity", "Reviews loaded: ${reviews.size}")
            reviewAdapter.updateData(reviews)
            updateReviewVisibility(reviews)
        })

        viewModel.errorMessage.observe(this, Observer { message ->
            Log.e("ProductDetailActivity", message)
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        })
    }

    private fun displayProductDetails(product: Product) {
        try {
            binding.productBrandText.text = product.brandName
            binding.productNameText.text = product.productName
            binding.forWhatPetType.text = product.petType
            binding.productCategory.text = product.category
            binding.productDescriptionText.text = product.description
            binding.reviewAverage.text = String.format("%.1f", product.averageRating)
            binding.reviewersCount.text = formatReviewCount(product.reviewCount)
            binding.percentageOfUser.text = "${product.percentageOfUsers}%"
            loadProductImage(product.imageUrl ?: "")
            binding.ratingBarProduct.rating = product.averageRating.toFloat() // Set rating bar
        } catch (e: Exception) {
            Log.e("ProductDetailActivity", "Error displaying product details", e)
            Toast.makeText(this, "Failed to display product details", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun formatReviewCount(count: Int) = if (count > 1000) "${count / 1000}k" else "$count"

    private fun loadProductImage(imageUrl: String) {
        try {
            Glide.with(this).load(imageUrl).into(binding.productPicture)
        } catch (e: Exception) {
            Log.e("ProductDetailActivity", "Error loading product image", e)
        }
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
