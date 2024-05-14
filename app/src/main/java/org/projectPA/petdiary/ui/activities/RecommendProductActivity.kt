package org.projectPA.petdiary.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ActivityRecommendProductStepFourBinding
import org.projectPA.petdiary.model.Review
import java.util.Date

class RecommendProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecommendProductStepFourBinding
    private lateinit var firestore: FirebaseFirestore
    private var productId: String? = null
    private var rating: Double = 0.0
    private var usagePeriod: String? = null
    private var reviewText: String? = null
    private var recommend: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecommendProductStepFourBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        productId = intent.getStringExtra("productId")
        rating = intent.getDoubleExtra("rating", 0.0)
        usagePeriod = intent.getStringExtra("usagePeriod")
        reviewText = intent.getStringExtra("reviewText")

        binding.backToChooseProductButton.setOnClickListener {
            onBackPressed()
        }

        binding.icThumbsUpInactive.setOnClickListener {
            recommend = true
            binding.icThumbsUpInactive.setImageResource(R.drawable.ic_thumbs_up_active)
            binding.icThumbsDownInactive.setImageResource(R.drawable.ic_thumbs_down_inactive)
        }

        binding.icThumbsDownInactive.setOnClickListener {
            recommend = false
            binding.icThumbsDownInactive.setImageResource(R.drawable.ic_thumbs_down_active)
            binding.icThumbsUpInactive.setImageResource(R.drawable.ic_thumbs_up_inactive)
        }

        binding.nextButtonToRecommendProduct.setOnClickListener {
            submitReview()
        }
    }

    @SuppressLint("LongLogTag")
    private fun submitReview() {
        val review = Review(
            id = firestore.collection("reviews").document().id,
            productId = productId!!,
            rating = rating.toFloat(),
            usagePeriod = usagePeriod!!,
            reviewText = reviewText!!,
            recommend = recommend,
            reviewDate = Date(),
            timestamp = System.currentTimeMillis()
        )

        firestore.collection("reviews").document(review.id).set(review)
            .addOnSuccessListener {
                Toast.makeText(this, "Review submitted successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ProductDetailActivity::class.java).apply {
                    putExtra("productId", productId)
                }
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to submit review", Toast.LENGTH_SHORT).show()
            }
    }
}
