package org.projectPA.petdiary.view.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ActivityRecommendProductStepFourBinding
import org.projectPA.petdiary.model.Review
import org.projectPA.petdiary.viewmodel.RecommendProductViewModel
import java.util.Date

class RecommendProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecommendProductStepFourBinding
    private val viewModel: RecommendProductViewModel by viewModels()
    private var productId: String? = null
    private var rating: Double = 0.0
    private var usagePeriod: String? = null
    private var reviewText: String? = null
    private var recommend: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecommendProductStepFourBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            fetchAndSubmitReview()
        }

        observeViewModel()
    }

    private fun fetchAndSubmitReview() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let {
            val userId = it.uid
            val userNameFromAuth = it.displayName ?: "Anonymous"
            val userPhotoUrl = it.photoUrl?.toString() ?: ""

            FirebaseFirestore.getInstance().collection("user").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val userNameFromProfile = document.getString("name") ?: userNameFromAuth
                        val userPhotoUrlFromProfile = document.getString("imageUrl") ?: userPhotoUrl
                        submitReview(userId, userNameFromProfile, userPhotoUrlFromProfile)
                    } else {
                        submitReview(userId, userNameFromAuth, userPhotoUrl)
                    }
                }
                .addOnFailureListener {
                    submitReview(userId, userNameFromAuth, userPhotoUrl)
                }
        }
    }


    @SuppressLint("LongLogTag")
    private fun submitReview(userId: String, userName: String, userPhotoUrl: String) {
        Log.d("RecommendProductActivity", "Submitting Review: userId=$userId, userName=$userName, userPhotoUrl=$userPhotoUrl")
        val review = Review(
            id = FirebaseFirestore.getInstance().collection("reviews").document().id,
            productId = productId!!,
            userId = userId,
            userName = userName,
            userPhotoUrl = userPhotoUrl.ifEmpty { "default" },
            rating = rating.toFloat(),
            usagePeriod = usagePeriod!!,
            reviewText = reviewText!!,
            recommend = recommend,
            reviewDate = Date(),
        )

        viewModel.submitReview(review)
    }


    @SuppressLint("LongLogTag")
    private fun observeViewModel() {
        viewModel.reviewSubmitted.observe(this, Observer { submitted ->
            if (submitted) {
                Toast.makeText(this, "Review submitted successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ProductDetailActivity::class.java).apply {
                    putExtra("productId", productId)
                }
                Log.d("RecommendProductActivity", "Starting ProductDetailActivity with productId: $productId")
                startActivity(intent)
                finish()
            }
        })
    }

}
