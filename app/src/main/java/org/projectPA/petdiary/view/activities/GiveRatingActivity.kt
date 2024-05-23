package org.projectPA.petdiary.view.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import org.projectPA.petdiary.databinding.ActivityGiveRatingStepOneBinding
import org.projectPA.petdiary.model.Product
import org.projectPA.petdiary.viewmodel.GiveRatingViewModel

class GiveRatingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGiveRatingStepOneBinding
    private val viewModel: GiveRatingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGiveRatingStepOneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productId = intent.getStringExtra("productId")
        val brandName = intent.getStringExtra("brandName")
        val productName = intent.getStringExtra("productName")
        val petType = intent.getStringExtra("petType")
        val imageUrl = intent.getStringExtra("imageUrl")

        if (productId != null) {
            viewModel.loadProductDetails(productId)
        }

        displayProductDetails(brandName, productName, petType, imageUrl)

        binding.backToChooseProductButton.setOnClickListener {
            onBackPressed()
        }



        binding.nextButtonToUsageProduct.setOnClickListener {
            viewModel.setRating(binding.ratingBar.rating.toDouble())
            viewModel.navigateToUsageProductActivity(this)
        }

        setupObservers()
    }

    private fun setupObservers() {
        viewModel.product.observe(this, Observer { product ->
            if (product != null) {
                displayProductDetails(product)
            } else {
                Toast.makeText(this, "Failed to load product details", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayProductDetails(product: Product) {
        binding.brandName.text = product.brandName
        binding.productName.text = product.productName
        binding.productTypeAnimal.text = "For ${product.petType}"

        Glide.with(this).load(product.imageUrl).into(binding.productPictureRaviewDetailPage)
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
