package org.projectPA.petdiary.view.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import org.projectPA.petdiary.databinding.ActivityMoreReviewsBinding
import org.projectPA.petdiary.view.adapters.ReviewAdapter
import org.projectPA.petdiary.viewmodel.MoreReviewsViewModel

class MoreReviewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMoreReviewsBinding
    private val viewModel: MoreReviewsViewModel by viewModels()
    private lateinit var reviewAdapter: ReviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoreReviewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productId = intent.getStringExtra("productId") ?: ""
        val productName = intent.getStringExtra("productName") ?: ""

        if (productId.isEmpty()) {
            Toast.makeText(this, "Product ID is missing!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupRecyclerView(productId, productName)
        observeViewModel()

        viewModel.fetchAllReviews(productId)

        binding.backToProductDetailPage.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView(productId: String, productName: String) {
        reviewAdapter = ReviewAdapter(emptyList(), this, productId, productName)
        binding.listReview.layoutManager = LinearLayoutManager(this)
        binding.listReview.adapter = reviewAdapter
    }

    private fun observeViewModel() {
        viewModel.reviews.observe(this, Observer { reviews ->
            reviewAdapter.updateData(reviews)
        })

        viewModel.errorMessage.observe(this, Observer { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        })
    }
}
