package org.projectPA.petdiary.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ActivityUsageProductStepTwoBinding

class UsageProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUsageProductStepTwoBinding
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

        displayProductDetails(brandName, productName, petType, imageUrl)

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
