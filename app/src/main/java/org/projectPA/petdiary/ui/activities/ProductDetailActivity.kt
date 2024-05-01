package org.projectPA.petdiary.ui.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.projectPA.petdiary.databinding.ActivityProductDetailBinding
import org.projectPA.petdiary.model.Product
import org.projectPA.petdiary.ui.adapters.ReviewAdapter
import java.io.File

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private lateinit var productId: String
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var reviewAdapter: ReviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firestore and Storage
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        // Get product ID from intent
        productId = intent.getStringExtra("productId") ?: ""

        // Display product details
        displayProductDetails(productId)

        // Set up RecyclerView for reviews
        setupRecyclerView()
    }

    private fun displayProductDetails(productId: String) {
        firestore.collection("products").document(productId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val product = document.toObject(Product::class.java)
                    product?.let {
                        binding.productBrandText.text = it.brandName
                        binding.productNameText.text = it.productName
                        binding.forWhatPetType.text = it.petType
                        binding.productCategory.text = it.category
                        binding.productDescriptionText.text = it.description

                        // Load product image
                        loadProductImage(productId)
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    private fun loadProductImage(productId: String) {
        val storageRef = storage.reference
        val imagesRef = storageRef.child("images/$productId.jpg")

        // Load image into ImageView using Glide or any other image loading library
        // Example with Glide:
        // Glide.with(this).load(imagesRef).into(binding.productPicture)
    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = binding.listReview
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize an empty adapter for reviews
        reviewAdapter = ReviewAdapter(emptyList())
        recyclerView.adapter = reviewAdapter

        // Get reviews from Firestore and update the adapter
        firestore.collection("reviews").whereEqualTo("productId", productId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val reviews = querySnapshot.documents
                reviewAdapter.updateReviews(reviews)
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    companion object {
        private const val TAG = "ProductDetailActivity"
    }
}
