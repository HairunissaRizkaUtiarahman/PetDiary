package org.projectPA.petdiary.view.activities

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import org.projectPA.petdiary.R
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
    private lateinit var product: Product
    private val currentUserId: String by lazy { FirebaseAuth.getInstance().currentUser?.uid ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productId = intent.getStringExtra("productId") ?: ""

        if (productId.isEmpty()) {
            Toast.makeText(this, "Product ID is missing!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupRecyclerView()
        observeViewModel()

        refresh()

        binding.reviewButton.setOnClickListener {
            if (::product.isInitialized) {
                if (binding.reviewButton.isEnabled) {
                    val intent = Intent(this, GiveReviewActivity::class.java).apply {
                        putExtra("productId", productId)
                        putExtra("brandName", product.brandName)
                        putExtra("productName", product.productName)
                        putExtra("petType", product.petType)
                        putExtra("imageUrl", product.imageUrl)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "You already reviewed this product", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Product details are not yet loaded", Toast.LENGTH_SHORT).show()
            }
        }

        binding.seeMoreReviewLink.setOnClickListener {
            val intent = Intent(this, MoreReviewsActivity::class.java).apply {
                putExtra("productId", productId)
                putExtra("productName", binding.productNameText.text.toString())
            }
            startActivity(intent)
        }

        binding.backButton.setOnClickListener {
            val intent = Intent(this, ReviewHomePageActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        viewModel.product.observe(this, Observer { product ->
            product?.let {
                this.product = it
                displayProductDetails(it)
                showContent()
            } ?: run {
                Toast.makeText(this, "Failed to load product details", Toast.LENGTH_SHORT).show()
                finish()
            }
        })

        viewModel.reviews.observe(this, Observer { reviews ->
            val limitedReviews = reviews.takeIf { it.size > 5 }?.take(5) ?: reviews
            reviewAdapter.updateData(limitedReviews)
            updateReviewVisibility(reviews)
        })

        viewModel.errorMessage.observe(this, Observer { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        })

        viewModel.checkIfUserReviewed(productId, currentUserId)

        viewModel.hasReviewed.observe(this, Observer { hasReviewed ->
            binding.reviewButton.isEnabled = !hasReviewed
            Log.d("ProductDetailActivity", "Review button enabled: ${!hasReviewed}")
        })
    }

    private fun displayProductDetails(product: Product) {
        binding.productBrandText.text = product.brandName
        binding.productNameText.text = product.productName
        binding.forWhatPetType.text = product.petType
        binding.productCategory.text = product.category
        binding.productDescriptionText.text = product.desc
        binding.reviewAverage.text = String.format("%.1f", product.averageRating)
        binding.reviewersCount.text = product.reviewCount.toString()
        binding.percentageOfUser.text = "${product.percentageOfUsers}%"
        Glide.with(this).load(product.imageUrl).into(binding.productPicture)
        binding.ratingBarProduct.rating = product.averageRating.toFloat()

        // Update uploader review section
        binding.uploaderUsername.text = product.uploaderName
        binding.reviewDate.text = product.uploaderReviewDate.toString()
        binding.usageProduct.text = product.usageUploader
        binding.deskripsiReviewUploader.text = product.uploaderReview
        binding.ratingBarUploader.rating = product.ratingUploader.toFloat()


    }

    private fun setupRecyclerView() {
        reviewAdapter = ReviewAdapter(emptyList(), this, productId)
        binding.listReview.layoutManager = LinearLayoutManager(this)
        binding.listReview.adapter = reviewAdapter
        binding.listReview.setHasFixedSize(true) // Optimize RecyclerView
    }

    private fun updateReviewVisibility(reviews: List<Review>) {
        binding.ifThereIsNoReview.visibility = if (reviews.isEmpty()) View.VISIBLE else View.GONE
        binding.listReview.visibility = if (reviews.isEmpty()) View.GONE else View.VISIBLE
        binding.seeMoreReviewLink.visibility = if (reviews.size > 5) View.VISIBLE else View.GONE
    }

    private fun refresh() {
        showLoading()
        viewModel.fetchDataInParallel(productId, currentUserId)
    }

    private fun showLoading() {
        binding.loadingAnimation.visibility = View.VISIBLE
        (binding.loadingAnimation.drawable as? AnimationDrawable)?.start()
        binding.toolbar.visibility = View.GONE
        binding.mainContent.visibility = View.GONE
    }

    private fun showContent() {
        (binding.loadingAnimation.drawable as? AnimationDrawable)?.stop()
        binding.loadingAnimation.visibility = View.GONE
        binding.mainContent.visibility = View.VISIBLE
        binding.toolbar.visibility = View.VISIBLE
    }
}
