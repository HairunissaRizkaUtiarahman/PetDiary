package org.projectPA.petdiary.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ActivityGiveReviewBinding
import org.projectPA.petdiary.view.fragment.addreview.GiveRatingFragment
import org.projectPA.petdiary.viewmodel.GiveReviewViewModel


class GiveReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGiveReviewBinding
    private lateinit var viewModel: GiveReviewViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGiveReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(GiveReviewViewModel::class.java)

        val productId = intent.getStringExtra("productId") ?: ""
        viewModel.loadProductDetails(productId)

        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let {
            viewModel.loadCurrentUser(it.uid)
        }

        viewModel.product.observe(this) { product ->
            binding.apply {
                productName.text = product?.productName
                brandName.text = product?.brandName
                productTypeAnimal.text = product?.petType
                Glide.with(this@GiveReviewActivity)
                    .load(product?.imageUrl)
                    .into(productPictureRaviewDetailPage)
            }
        }

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.frame_layout, GiveRatingFragment())
            }
        }

        binding.backToChooseProductButton.setOnClickListener {
            onBackPressed()
        }
    }
}
