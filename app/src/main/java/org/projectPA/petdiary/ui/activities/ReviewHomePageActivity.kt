package org.projectPA.petdiary.ui.activities

import android.content.Intent
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ActivityReviewHomepageBinding
import org.projectPA.petdiary.fragment.HomeFragment
import org.projectPA.petdiary.fragment.ProfileFragment
import org.projectPA.petdiary.model.Product
import org.projectPA.petdiary.ui.adapters.ProductAdapter


class ReviewHomePageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewHomepageBinding
    private lateinit var productAdapter: ProductAdapter
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewHomepageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadDataFromFirestore()

        binding.catButton.setOnClickListener {
            val petType = "cat"
            val intent = Intent(this, ProductCategoriesPageActivity::class.java)
            intent.putExtra("petType", petType)
            startActivity(intent)
        }

        binding.dogButton.setOnClickListener {
            startActivity(Intent(this, ProductCategoriesPageActivity::class.java).apply {
                putExtra("petType", "dog")
            })
        }

        binding.rabbitButton.setOnClickListener {
            startActivity(Intent(this, ProductCategoriesPageActivity::class.java).apply {
                putExtra("petType", "rabbit")
            })
        }

        binding.hamsterButton.setOnClickListener {
            startActivity(Intent(this, ProductCategoriesPageActivity::class.java).apply {
                putExtra("petType", "hamster")
            })
        }

        binding.fishButton.setOnClickListener {
            startActivity(Intent(this, ProductCategoriesPageActivity::class.java).apply {
                putExtra("petType", "fish")
            })
        }
        binding.birdButton.setOnClickListener {
            startActivity(Intent(this, ProductCategoriesPageActivity::class.java).apply {
                putExtra("petType", "bird")
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
            // Show the add button fragment with animation
            val fragment = AddButtonFragment()
            fragment.show(supportFragmentManager, fragment.tag)
        }
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

    private fun loadDataFromFirestore() {
        firestore = FirebaseFirestore.getInstance()
        firestore.collection("products")
            .get()
            .addOnSuccessListener { documents ->
                val products = documents.mapNotNull { it.toObject(Product::class.java) }
                productAdapter.updateData(products)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error loading products: ", e)
            }
    }




}

