package org.projectPA.petdiary.view.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ActivityWriteReviewStepThreeBinding
import org.projectPA.petdiary.viewmodel.WriteReviewViewModel

class WriteReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWriteReviewStepThreeBinding
    private val viewModel: WriteReviewViewModel by viewModels()
    private var productId: String? = null
    private var rating: Double = 0.0
    private var usagePeriod: String? = null

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
            viewModel.setReviewText(binding.reviewEditText.text.toString())
            viewModel.submitReview()
        }

        setupTextWatcher(binding.reviewEditText, binding.reviewTextInputLayout)
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

    private fun setupTextWatcher(editText: TextInputEditText, textInputLayout: TextInputLayout) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString()
                if (text.isEmpty()) {
                    textInputLayout.hint = getString(R.string.write_you_review_here)
                } else {
                    textInputLayout.hint = null
                }
                // Optional: additional validation
                val words = text.trim().split("\\s+".toRegex())
                binding.nextButtonToRecommendProduct.isEnabled = words.size >= 10
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observeViewModel() {
        viewModel.reviewText.observe(this, Observer {
            // Handle review text changes if needed
        })

        viewModel.navigateToRecommend.observe(this, Observer { navigate ->
            if (navigate) {
                val reviewText = viewModel.reviewText.value
                val intent = Intent(this, RecommendProductActivity::class.java).apply {
                    putExtra("productId", productId)
                    putExtra("rating", rating)
                    putExtra("usagePeriod", usagePeriod)
                    putExtra("reviewText", reviewText)
                    putExtra("brandName", binding.brandName.text)
                    putExtra("productName", binding.productName.text)
                    putExtra("petType", binding.productTypeAnimal.text.toString().substring(4))
                    putExtra("imageUrl", intent.getStringExtra("imageUrl"))
                }
                startActivity(intent)
                viewModel.doneNavigating()
            }
        })

        viewModel.errorMessage.observe(this, Observer { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        })
    }
}
