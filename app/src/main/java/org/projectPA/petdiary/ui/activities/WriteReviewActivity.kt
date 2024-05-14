package org.projectPA.petdiary.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import org.projectPA.petdiary.databinding.ActivityWriteReviewStepThreeBinding

class WriteReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWriteReviewStepThreeBinding
    private var productId: String? = null
    private var rating: Double = 0.0
    private var usagePeriod: String? = null
    private var reviewText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteReviewStepThreeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productId = intent.getStringExtra("productId")
        rating = intent.getDoubleExtra("rating", 0.0)
        usagePeriod = intent.getStringExtra("usagePeriod")
        val brandName = intent.getStringExtra("brandName")
        val productName = intent.getStringExtra("productName")
        val petType = intent.getStringExtra("petType")
        val imageUrl = intent.getStringExtra("imageUrl")

        displayProductDetails(brandName, productName, petType, imageUrl)

        binding.backToChooseProductButton.setOnClickListener {
            onBackPressed()
        }

        binding.nextButtonToRecommendProduct.setOnClickListener {
            reviewText = binding.reviewEditText.text.toString()
            if (reviewText.isNullOrBlank()) {
                Toast.makeText(this, "Please write a review", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, RecommendProductActivity::class.java).apply {
                    putExtra("productId", productId)
                    putExtra("rating", rating)
                    putExtra("usagePeriod", usagePeriod)
                    putExtra("reviewText", reviewText)
                    putExtra("brandName", brandName)
                    putExtra("productName", productName)
                    putExtra("petType", petType)
                    putExtra("imageUrl", imageUrl)
                }
                startActivity(intent)
            }
        }
    }

    private fun displayProductDetails(brandName: String?, productName: String?, petType: String?, imageUrl: String?) {
        binding.brandName.text = brandName
        binding.productName.text = productName
        binding.productTypeAnimal.text = "For $petType"

        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(this).load(imageUrl).into(binding.productPictureRaviewDetailPage)
        }
    }
}
