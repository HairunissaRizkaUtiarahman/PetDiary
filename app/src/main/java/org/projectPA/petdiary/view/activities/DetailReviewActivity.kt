package org.projectPA.petdiary.view.activities

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
        } else {
            Toast.makeText(this, "Invalid product or review ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.backToProductDetailButton.setOnClickListener {
            finish()
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
                binding.reviewDate.text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(it.reviewDate)
                binding.deskripsiReview.text = it.reviewText
                binding.ratingBar2.rating = it.rating
                binding.usagePeriodReview.text = it.usagePeriod
                binding.recomendedOrNotText.text = if (it.rating >= 4) "I Recommend This Product" else "Not Recommended"
                if (it.userPhotoUrl == "default") {
                    binding.userPhotoProfile.setImageResource(R.drawable.ic_users)
                } else {
                    Glide.with(this).load(it.userPhotoUrl).into(binding.userPhotoProfile)
                }
            }
        })

        viewModel.errorMessage.observe(this, Observer { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        })
    }
}
