package org.projectPA.petdiary.view.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
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
    private var recommend: Boolean? = null // Change to nullable Boolean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecommendProductStepFourBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productId = intent.getStringExtra("productId")
        rating = intent.getDoubleExtra("rating", 0.0)
        usagePeriod = intent.getStringExtra("usagePeriod")
        reviewText = intent.getStringExtra("reviewText")
        val brandName = intent.getStringExtra("brandName")
        val productName = intent.getStringExtra("productName")
        val petType = intent.getStringExtra("petType")
        val imageUrl = intent.getStringExtra("imageUrl")

        displayProductDetails(brandName, productName, petType, imageUrl)

        binding.backToChooseProductButton.setOnClickListener {
            onBackPressed()
        }

        binding.prevButtonToWriteReview.setOnClickListener {
            onBackPressed()
        }

        binding.icThumbsUpInactive.setOnClickListener {
            recommend = true
            binding.icThumbsUpInactive.setImageResource(R.drawable.ic_thumbs_up_active)
            binding.icThumbsDownInactive.setImageResource(R.drawable.ic_thumbs_down_inactive)
            updateSubmitButtonState()
        }

        binding.icThumbsDownInactive.setOnClickListener {
            recommend = false
            binding.icThumbsDownInactive.setImageResource(R.drawable.ic_thumbs_down_active)
            binding.icThumbsUpInactive.setImageResource(R.drawable.ic_thumbs_up_inactive)
            updateSubmitButtonState()
        }

        binding.nextButtonToRecommendProduct.setOnClickListener {
            fetchAndSubmitReview()
        }

        observeViewModel()
    }

    private fun displayProductDetails(brandName: String?, productName: String?, petType: String?, imageUrl: String?) {
        binding.brandName.text = brandName
        binding.productName.text = productName
        binding.productTypeAnimal.text = "For $petType"

        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(this).load(imageUrl).into(binding.productPictureRaviewDetailPage)
        }
    }

    private fun fetchAndSubmitReview() {
        if (recommend == null) {
            Toast.makeText(this, "Please select if you recommend the product or not.", Toast.LENGTH_SHORT).show()
            return
        }

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
            recommend = recommend!!, // Recommend should not be null here
            reviewDate = Timestamp.now(),
        )

        viewModel.submitReview(review)
    }

    private fun updateSubmitButtonState() {
        binding.nextButtonToRecommendProduct.isEnabled = recommend != null
    }

    @SuppressLint("LongLogTag")
    private fun observeViewModel() {
        viewModel.reviewSubmitted.observe(this, Observer { submitted ->
            if (submitted) {
                Toast.makeText(this, "Review submitted successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ProductDetailActivity::class.java).apply {
                    putExtra("productId", productId)
                    putExtra("sourceActivity", "RecommendProductActivity")
                }
                Log.d("RecommendProductActivity", "Starting ProductDetailActivity with productId: $productId")
                startActivity(intent)
                finish()
            }
        })
    }
}
