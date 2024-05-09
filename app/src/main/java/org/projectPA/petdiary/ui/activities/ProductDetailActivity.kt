package org.projectPA.petdiary.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.projectPA.petdiary.databinding.ActivityProductDetailBinding
import org.projectPA.petdiary.model.Product
import org.projectPA.petdiary.model.Review
import org.projectPA.petdiary.ui.adapters.ReviewAdapter

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private lateinit var productId: String
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var reviewAdapter: ReviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)

        binding.backButton.setOnClickListener {
            val cameFromFillInfo = intent.getBooleanExtra("fromFillProductInfo", false)
            if (cameFromFillInfo) {
                val intent = Intent(this, ProductPageActivity::class.java)
                startActivity(intent)
            } else {
                onBackPressed()
            }
        }

        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        productId = intent.getStringExtra("productId") ?: ""

        displayProductDetails(productId)
        setupRecyclerView()
        loadReviews() // Load reviews on activity start
    }

    @SuppressLint("SetTextI18n")
    private fun displayProductDetails(productId: String) {
        firestore.collection("products").document(productId).get()
            .addOnSuccessListener { document ->
                val product = document.toObject(Product::class.java)
                product?.let {
                    Log.d(TAG, "Category: ${it.category}")
                    binding.productBrandText.text = it.brandName
                    binding.productNameText.text = it.productName
                    binding.forWhatPetType.text = it.petType
                    binding.productCategory.text = it.category
                    binding.productDescriptionText.text = it.description
                    binding.reviewAverage.text = it.averageRating.toString()
                    binding.reviewersCount.text = formatReviewCount(it.reviewCount)
                    binding.percentageOfUser.text = "${it.percentageOfUsers}%"
                    loadProductImage(it.imageUrl ?: "")
                }
            }.addOnFailureListener { e ->
                Log.e(TAG, "Failed to load product details", e)
            }
    }

    private fun formatReviewCount(count: Int) = if (count > 1000) "${count / 1000}k" else "$count"

    private fun loadProductImage(imageUrl: String) {
        Glide.with(this).load(imageUrl).into(binding.productPicture)
    }

    private fun setupRecyclerView() {
        binding.listReview.layoutManager = LinearLayoutManager(this)
        reviewAdapter = ReviewAdapter(emptyList())
        binding.listReview.adapter = reviewAdapter
    }

    private fun loadReviews() {
        firestore.collection("reviews").whereEqualTo("productId", productId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val reviews = querySnapshot.documents.mapNotNull { it.toObject(Review::class.java) }
                reviewAdapter.updateReviews(reviews)
                updateReviewVisibility(reviews)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents: ", exception)
            }
    }

    private fun updateReviewVisibility(reviews: List<Review>) {
        binding.ifThereIsNoReview.visibility = if (reviews.isEmpty()) View.VISIBLE else View.GONE
        binding.listReview.visibility = if (reviews.isEmpty()) View.GONE else View.VISIBLE
        binding.seeMoreReviewLink.visibility = if (reviews.size > 5) View.VISIBLE else View.GONE
    }

    companion object {
        private const val TAG = "ProductDetailActivity"
    }
}
