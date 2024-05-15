package org.projectPA.petdiary.view.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
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

        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        setupRecyclerView()
        observeViewModel()

        viewModel.fetchProductDetails(productId)
    }

    private fun observeViewModel() {
        viewModel.product.observe(this, Observer { product ->
            product?.let { displayProductDetails(it) }
        })

        viewModel.reviews.observe(this, Observer { reviews ->
            reviewAdapter.updateData(reviews)
            updateReviewVisibility(reviews)
        })

        viewModel.errorMessage.observe(this, Observer { message ->
            Log.e(TAG, message)
        })
    }

    @SuppressLint("SetTextI18n")
    private fun displayProductDetails(product: Product) {
        binding.productBrandText.text = product.brandName
        binding.productNameText.text = product.productName
        binding.forWhatPetType.text = product.petType
        binding.productCategory.text = product.category
        binding.productDescriptionText.text = product.description
        binding.reviewAverage.text = product.averageRating.toString()
        binding.reviewersCount.text = formatReviewCount(product.reviewCount)
        binding.percentageOfUser.text = "${product.percentageOfUsers}%"
        loadProductImage(product.imageUrl ?: "")
    }

    private fun formatReviewCount(count: Int) = if (count > 1000) "${count / 1000}k" else "$count"

    private fun loadProductImage(imageUrl: String) {
        Glide.with(this).load(imageUrl).into(binding.productPicture)
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

    companion object {
        private const val TAG = "ProductDetailActivity"
    }
}
