package org.projectPA.petdiary.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import org.projectPA.petdiary.databinding.ActivityProductPageBinding
import org.projectPA.petdiary.model.Product
import org.projectPA.petdiary.ui.adapters.ProductAdapter

class ProductPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductPageBinding // Ensure you have the correct binding class
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadProductsFromFirestore()
    }

    private fun setupRecyclerView() {
        // Set up the RecyclerView with a GridLayoutManager to display 2 items per row
        binding.listProduct.layoutManager = GridLayoutManager(this, 2)
        productAdapter = ProductAdapter(emptyList()) { productId ->
            val intent = Intent(this, ProductDetailActivity::class.java)
            intent.putExtra("productId", productId)
            startActivity(intent)
        }
        binding.listProduct.adapter = productAdapter
    }

    private fun loadProductsFromFirestore() {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("products")
            .get()
            .addOnSuccessListener { documents ->
                val products = documents.mapNotNull { document ->
                    document.toObject(Product::class.java)
                }
                productAdapter.updateData(products)
            }
            .addOnFailureListener { e ->
                Log.e("ProductPageActivity", "Error loading products: ", e)
            }
    }
}
