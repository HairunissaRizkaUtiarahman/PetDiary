package org.projectPA.petdiary.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ActivityDetailReviewBinding
import org.projectPA.petdiary.model.Review
import java.text.SimpleDateFormat
import java.util.Locale

class DetailReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailReviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productId = intent.getStringExtra("productId") ?: ""
        val productName = intent.getStringExtra("productName") ?: ""
        val review = intent.getParcelableExtra<Review>("review") ?: return

        displayReviewDetails(productName, review)

        binding.backToProductDetailButton.setOnClickListener {
            finish()
        }
    }

    private fun displayReviewDetails(productName: String, review: Review) {
        binding.brandName.text = productName
        binding.username.text = review.userName
        binding.reviewDate.text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(review.reviewDate)
        binding.deskripsiReview.text = review.reviewText
        binding.ratingBar2.rating = review.rating
        binding.usagePeriodReview.text = review.usagePeriod
        binding.reviewDate.text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(review.reviewDate)
        binding.recomendedOrNotText.text = if (review.rating >= 4) "Recommended" else "Not Recommended"

        if (review.userPhotoUrl == "default") {
            binding.userPhotoProfile.setImageResource(R.drawable.ic_users)
        } else {
            Glide.with(this).load(review.userPhotoUrl).into(binding.userPhotoProfile)
        }
    }
}
