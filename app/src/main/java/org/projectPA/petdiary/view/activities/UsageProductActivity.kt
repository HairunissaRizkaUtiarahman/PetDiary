package org.projectPA.petdiary.view.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ActivityUsageProductStepTwoBinding
import org.projectPA.petdiary.viewmodel.UsageProductViewModel

class UsageProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUsageProductStepTwoBinding
    private val viewModel: UsageProductViewModel by viewModels()

    private var productId: String? = null
    private var rating: Double = 0.0
    private var usagePeriod: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsageProductStepTwoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productId = intent.getStringExtra("productId")
        rating = intent.getDoubleExtra("rating", 0.0)
        val brandName = intent.getStringExtra("brandName")
        val productName = intent.getStringExtra("productName")
        val petType = intent.getStringExtra("petType")
        val imageUrl = intent.getStringExtra("imageUrl")

        viewModel.setProductDetails(brandName, productName, petType, imageUrl)

        val usageOptions = resources.getStringArray(R.array.usage_options)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, usageOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.usageDropdown.adapter = adapter

        binding.backToChooseProductButton.setOnClickListener {
            onBackPressed()
        }

        binding.nextButtonToRecommendProduct.setOnClickListener {
            usagePeriod = binding.usageDropdown.selectedItem.toString()
            if (usagePeriod.isNullOrBlank()) {
                Toast.makeText(this, "Please select a usage period", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, WriteReviewActivity::class.java).apply {
                    putExtra("productId", productId)
                    putExtra("rating", rating)
                    putExtra("usagePeriod", usagePeriod)
                    putExtra("brandName", brandName)
                    putExtra("productName", productName)
                    putExtra("petType", petType)
                    putExtra("imageUrl", imageUrl)
                }
                startActivity(intent)
            }
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.productDetails.observe(this, Observer { details ->
            binding.brandName.text = details.brandName
            binding.productName.text = details.productName
            binding.productTypeAnimal.text = "For ${details.petType}"

            if (!details.imageUrl.isNullOrEmpty()) {
                Glide.with(this).load(details.imageUrl).into(binding.productPictureRaviewDetailPage)
            }
        })

//        viewModel.usagePeriod.observe(this, Observer { usagePeriod ->
//            // Handle usage period change if needed
//        })
    }
}
