package org.projectPA.petdiary.view.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ActivityReviewHomepageBinding
import org.projectPA.petdiary.view.fragment.HomeFragment
import org.projectPA.petdiary.view.fragment.ProfileFragment
import org.projectPA.petdiary.view.adapters.ProductAdapter
import org.projectPA.petdiary.viewmodel.ReviewHomePageViewModel

class ReviewHomePageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewHomepageBinding
    private lateinit var productAdapter: ProductAdapter
    private val viewModel: ReviewHomePageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewHomepageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeViewModel()

        binding.catButton.setOnClickListener {
            val petType = "cat"
            val intent = Intent(this, ProductCategoriesPageActivity::class.java)
            intent.putExtra("petType", petType)
            startActivity(intent)
        }

        binding.dogButton.setOnClickListener {
            startActivity(Intent(this, ProductCategoriesPageActivity::class.java).apply {
                putExtra("petType", "Dog")
            })
        }

        binding.rabbitButton.setOnClickListener {
            startActivity(Intent(this, ProductCategoriesPageActivity::class.java).apply {
                putExtra("petType", "Rabbit")
            })
        }

        binding.hamsterButton.setOnClickListener {
            startActivity(Intent(this, ProductCategoriesPageActivity::class.java).apply {
                putExtra("petType", "Hamster")
            })
        }

        binding.fishButton.setOnClickListener {
            startActivity(Intent(this, ProductCategoriesPageActivity::class.java).apply {
                putExtra("petType", "Fish")
            })
        }

        binding.birdButton.setOnClickListener {
            startActivity(Intent(this, ProductCategoriesPageActivity::class.java).apply {
                putExtra("petType", "Bird")
            })
        }

        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.home -> replaceFragment(HomeFragment())
                R.id.profile -> replaceFragment(ProfileFragment())
            }
            true
        }

        binding.addButton.setOnClickListener {
            val fragment = AddButtonFragment()
            fragment.show(supportFragmentManager, fragment.tag)
        }

        // Load products when activity is created
        viewModel.loadProducts()
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(emptyList()) { productId ->
            val intent = Intent(this, ProductDetailActivity::class.java).apply {
                putExtra("productId", productId)
            }
            startActivity(intent)
        }
        binding.listMostReviewProduct.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.listMostReviewProduct.adapter = productAdapter
    }

    private fun observeViewModel() {
        viewModel.products.observe(this, Observer { products ->
            productAdapter.updateData(products)
        })
    }
}
