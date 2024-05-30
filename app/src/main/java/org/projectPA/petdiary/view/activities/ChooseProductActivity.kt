package org.projectPA.petdiary.view.activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ActivityChooseProductBinding
import org.projectPA.petdiary.view.adapters.ProductAdapter
import org.projectPA.petdiary.view.fragments.SortButtonProductFragment
import org.projectPA.petdiary.viewmodel.ChooseProductViewModel

class ChooseProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChooseProductBinding
    private lateinit var productAdapter: ProductAdapter
    private val viewModel: ChooseProductViewModel by viewModels()
    private var currentUserId: String = FirebaseAuth.getInstance().currentUser?.uid ?: ""

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

        // Search functionality
        binding.searchProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null && query.isNotEmpty()) {
                    viewModel.searchProducts(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    viewModel.clearSearch()
                } else {
                    viewModel.searchProducts(newText)
                }
                return true
            }
        })

        binding.backToReviewHomepageButton.setOnClickListener {
            onBackPressed()
        }

        // Show sort options when button is clicked
        binding.sortButtonProduct.setOnClickListener {
            val sortButtonFragment = SortButtonProductFragment { sortOption ->
                viewModel.sortProducts(sortOption)
            }
            sortButtonFragment.show(supportFragmentManager, sortButtonFragment.tag)
        }
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(emptyList()) { productId ->
            checkIfUserReviewed(productId, currentUserId)
        }
        binding.listProduct.layoutManager = GridLayoutManager(this, 2)
        binding.listProduct.adapter = productAdapter
    }

    private fun checkIfUserReviewed(productId: String, userId: String) {
        viewModel.checkIfUserReviewed(productId, userId).observe(this, Observer { hasReviewed ->
            if (hasReviewed) {
                showAlreadyReviewedPopup()
            } else {
                val intent = Intent(this, GiveRatingActivity::class.java).apply {
                    putExtra("productId", productId)
                }
                startActivity(intent)
            }
        })
    }

    private fun showAlreadyReviewedPopup() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.pop_up_message_already_reviewed, null)
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogView)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val closeButton: Button = dialogView.findViewById(R.id.close_button)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
