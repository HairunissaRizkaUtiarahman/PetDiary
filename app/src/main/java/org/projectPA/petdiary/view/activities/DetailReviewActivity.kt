package org.projectPA.petdiary.view.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ActivityDetailReviewBinding
import org.projectPA.petdiary.viewmodel.DetailReviewViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class DetailReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailReviewBinding
    private val viewModel: DetailReviewViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productId = intent.getStringExtra("productId") ?: ""
        val reviewId = intent.getStringExtra("reviewId") ?: ""

        if (productId.isNotEmpty() && reviewId.isNotEmpty()) {
            viewModel.fetchProductDetails(productId)
            viewModel.fetchReviewDetails(reviewId)
            viewModel.fetchCommentsCount(reviewId)
        } else {
            Toast.makeText(this, "Invalid product or review ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.backToProductDetailButton.setOnClickListener {
            finish()
        }

        binding.addCommentButton.setOnClickListener {
            val intent = Intent(this, ReviewCommentActivity::class.java).apply {
                putExtra("reviewId", reviewId)
            }
            startActivity(intent)
        }

        binding.seeCommentButton.setOnClickListener {
            val intent = Intent(this, ReviewCommentActivity::class.java).apply {
                putExtra("reviewId", reviewId)
            }
            startActivity(intent)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.product.observe(this, Observer { product ->
            product?.let {
                binding.brandName.text = it.brandName
                binding.productName.text = it.productName
                binding.productTypeAnimal.text = it.petType
                Glide.with(this).load(it.imageUrl).into(binding.productPictureRaviewDetailPage)
            }
        })

        viewModel.review.observe(this, Observer { review ->
            review?.let {
                binding.username.text = it.userName
                binding.reviewDate.text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(it.reviewDate?.toDate())
                binding.deskripsiReview.text = it.reviewText
                binding.ratingBar2.rating = it.rating
                binding.usagePeriodReview.text = it.usagePeriod
                binding.recomendedOrNotText.text = if (it.recommend) "I Recommend This Product" else "Not Recommended"
                Glide.with(this).load(it.userPhotoUrl).into(binding.userPhotoProfile)
            }
        })

        viewModel.commentsCount.observe(this, Observer { count ->
            binding.jumlahComment.text = count.toString()
        })

        viewModel.errorMessage.observe(this, Observer { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        })
    }
}
