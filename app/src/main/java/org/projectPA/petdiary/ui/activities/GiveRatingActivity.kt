package org.projectPA.petdiary.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import org.projectPA.petdiary.databinding.ActivityGiveRatingStepOneBinding
import org.projectPA.petdiary.model.Product

class GiveRatingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGiveRatingStepOneBinding
    private lateinit var firestore: FirebaseFirestore
    private var productId: String? = null
    private var product: Product? = null
    private var rating: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGiveRatingStepOneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        productId = intent.getStringExtra("productId")

        if (productId != null) {
            loadProductDetails(productId!!)
        }

        binding.backToChooseProductButton.setOnClickListener {
            onBackPressed()
        }

        binding.nextButtonToUsageProduct.setOnClickListener {
            rating = binding.ratingBar.rating.toDouble()
            if (rating == 0.0) {
                Toast.makeText(this, "Please provide a rating", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, UsageProductActivity::class.java).apply {
                    putExtra("productId", productId)
                    putExtra("rating", rating)
                    putExtra("brandName", product?.brandName)
                    putExtra("productName", product?.productName)
                    putExtra("petType", product?.petType)
                    putExtra("imageUrl", product?.imageUrl)
                }
                startActivity(intent)
            }
        }
    }

    private fun loadProductDetails(productId: String) {
        firestore.collection("products").document(productId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    product = document.toObject(Product::class.java)
                    product?.let { displayProductDetails(it) }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load product details", Toast.LENGTH_SHORT).show()
            }
    }

    private fun displayProductDetails(product: Product) {
        binding.brandName.text = product.brandName
        binding.productName.text = product.productName
        binding.productTypeAnimal.text = "For ${product.petType}"

        Glide.with(this).load(product.imageUrl).into(binding.productPictureRaviewDetailPage)
    }
}
