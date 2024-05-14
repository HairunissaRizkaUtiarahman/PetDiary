package org.projectPA.petdiary.view.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import org.projectPA.petdiary.databinding.ActivityProductPageBinding
import org.projectPA.petdiary.model.Product
import org.projectPA.petdiary.view.adapters.ProductAdapter
import org.projectPA.petdiary.viewmodel.ProductPageViewModel

class ProductPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductPageBinding
    private lateinit var productAdapter: ProductAdapter
    private val viewModel: ProductPageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Back button functionality
        binding.backToProductCategoriesPage.setOnClickListener {
            onBackPressed()
        }

        // Retrieve petType and category from Intent
        val petType = intent.getStringExtra("petType")
        val category = intent.getStringExtra("category")

        // Setup RecyclerView
        setupRecyclerView()

        // Observe ViewModel data
        observeViewModel()

        // Validate and load products
        if (petType != null && category != null) {
            viewModel.loadProductsFromFirestore(petType, category)
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
        binding.listProduct.layoutManager = GridLayoutManager(this, 2)
        binding.listProduct.adapter = productAdapter
    }

    private fun observeViewModel() {
        viewModel.products.observe(this, Observer { products ->
            productAdapter.updateData(products)
        })

        viewModel.errorMessage.observe(this, Observer { message ->
            Log.e("ProductPageActivity", message)
        })
    }
}
