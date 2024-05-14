package org.projectPA.petdiary.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import org.projectPA.petdiary.databinding.ActivityProductPageBinding
import org.projectPA.petdiary.model.Product
import org.projectPA.petdiary.ui.adapters.ProductAdapter

class ProductPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductPageBinding
    private lateinit var productAdapter: ProductAdapter
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Back button functionality
        binding.backToProductCategoriesPage.setOnClickListener {
            onBackPressed()
        }

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Retrieve petType and category from Intent
        val petType = intent.getStringExtra("petType")
        val category = intent.getStringExtra("category")

        // Setup RecyclerView
        setupRecyclerView()

        // Validate and load products
        if (petType != null && category != null) {
            loadProductsFromFirestore(petType, category)
        } else {
            Log.e("ProductPageActivity", "Missing petType or category. Cannot load products.")
        }
    }

    private fun setupRecyclerView() {

        productAdapter = ProductAdapter(emptyList()) { productId ->
            val intent = Intent(this, ProductDetailActivity::class.java).apply {
                putExtra("productId", productId)
            }
            startActivity(intent)
        }
        //binding.listProduct.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.listProduct.layoutManager = GridLayoutManager(this, 2)
        binding.listProduct.adapter = productAdapter
    }

    private fun loadProductsFromFirestore(petType: String, category: String) {
        firestore.collection("products")
            .whereEqualTo("petType", petType)
            .whereEqualTo("category", category)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.d("ProductPageActivity", "No products found for Pet Type: $petType, Category: $category")
                    productAdapter.updateData(emptyList())
                    return@addOnSuccessListener
                }

                val products = documents.mapNotNull { document ->
                    document.toObject(Product::class.java)
                }
                if (products.isNotEmpty()) {
                    productAdapter.updateData(products)
                    Log.d("ProductPageActivity", "Loaded ${products.size} products for Pet Type: $petType, Category: $category")
                } else {
                    Log.d("ProductPageActivity", "Products list is empty after mapping. Check data class and document structure.")
                }
            }
            .addOnFailureListener { e ->
                Log.e("ProductPageActivity", "Error loading products: ", e)
            }
    }

}
