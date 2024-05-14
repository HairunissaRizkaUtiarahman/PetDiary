package org.projectPA.petdiary.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import org.projectPA.petdiary.databinding.ActivityChooseProductBinding
import org.projectPA.petdiary.model.Product
import org.projectPA.petdiary.ui.adapters.ProductAdapter
import androidx.appcompat.widget.SearchView

class ChooseProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChooseProductBinding
    private lateinit var productAdapter: ProductAdapter
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        setupRecyclerView()
        loadProductsFromFirestore()

        binding.searchProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null && query.isNotEmpty()) {
                    searchProducts(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    loadProductsFromFirestore()
                } else {
                    searchProducts(newText)
                }
                return true
            }
        })

        binding.backToReviewHomepageButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(emptyList()) { productId ->
            val intent = Intent(this, GiveRatingActivity::class.java).apply {
                putExtra("productId", productId)
            }
            startActivity(intent)
        }
        binding.listProduct.layoutManager = GridLayoutManager(this, 2)
        binding.listProduct.adapter = productAdapter
    }

    private fun loadProductsFromFirestore() {
        firestore.collection("products")
            .get()
            .addOnSuccessListener { documents ->
                val products = documents.mapNotNull { it.toObject(Product::class.java) }
                productAdapter.updateData(products)
            }
            .addOnFailureListener { e ->
                Log.e("ChooseProductActivity", "Error loading products: ", e)
            }
    }

    private fun searchProducts(query: String) {
        val productsRef = firestore.collection("products")

        val queryProduct = productsRef
            .whereGreaterThanOrEqualTo("productName", query)
            .whereLessThanOrEqualTo("productName", query + "\uf8ff")

        val queryBrand = productsRef
            .whereGreaterThanOrEqualTo("brandName", query)
            .whereLessThanOrEqualTo("brandName", query + "\uf8ff")

        val combinedProducts = mutableSetOf<Product>()

        queryProduct.get().addOnSuccessListener { documents ->
            val productsByName = documents.mapNotNull { it.toObject(Product::class.java) }
            combinedProducts.addAll(productsByName)

            queryBrand.get().addOnSuccessListener { documents ->
                val productsByBrand = documents.mapNotNull { it.toObject(Product::class.java) }
                combinedProducts.addAll(productsByBrand)

                productAdapter.updateData(combinedProducts.toList())
            }.addOnFailureListener { e ->
                Log.e("ChooseProductActivity", "Error searching products by brand: ", e)
            }
        }.addOnFailureListener { e ->
            Log.e("ChooseProductActivity", "Error searching products by name: ", e)
        }
    }
}
