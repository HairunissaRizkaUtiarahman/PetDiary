package org.projectPA.petdiary.view.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.appcompat.widget.SearchView
import org.projectPA.petdiary.databinding.ActivityChooseProductBinding
import org.projectPA.petdiary.model.Product
import org.projectPA.petdiary.view.adapters.ProductAdapter
import org.projectPA.petdiary.viewmodel.ChooseProductViewModel

class ChooseProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChooseProductBinding
    private lateinit var productAdapter: ProductAdapter
    private val viewModel: ChooseProductViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        viewModel.products.observe(this, Observer { products ->
            productAdapter.updateData(products)
        })

        viewModel.error.observe(this, Observer { errorMessage ->
            Log.e("ChooseProductActivity", errorMessage)
        })

        binding.searchProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null && query.isNotEmpty()) {
                    viewModel.searchProducts(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    viewModel.loadProducts()
                } else {
                    viewModel.searchProducts(newText)
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
}
