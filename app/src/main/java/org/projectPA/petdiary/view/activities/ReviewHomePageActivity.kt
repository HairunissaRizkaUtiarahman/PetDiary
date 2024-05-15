package org.projectPA.petdiary.view.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ActivityReviewHomepageBinding
import org.projectPA.petdiary.view.fragment.HomeFragment
import org.projectPA.petdiary.view.fragment.ProfileFragment

//import org.projectPA.petdiary.ui.adapters.ProductAdapter

class ReviewHomePageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewHomepageBinding
//    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewHomepageBinding.inflate(layoutInflater)
        setContentView(binding.root)


//        adapter = ProductAdapter(this, getProductList())
//        binding.recyclerViewHorizontal.apply {
//            layoutManager = LinearLayoutManager(this@ReviewHomePageActivity, LinearLayoutManager.HORIZONTAL, false)
//            this.adapter = this@ReviewHomePageActivity.adapter
//        }


        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.home -> replaceFragment(HomeFragment())
                R.id.profile -> replaceFragment(ProfileFragment())
            }
            true
        }


        binding.addButton.setOnClickListener {
            // Show the add button fragment with animation
            val fragment = FragmentAddButton()
            fragment.show(supportFragmentManager, fragment.tag)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }

    class FragmentAddButton : BottomSheetDialogFragment() {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.fragment_add_button, container, false)
        }
    }

//    private fun getProductList(): List<Product> {
//        // Dummy data for RecyclerView (you can replace this with your actual data)
//        return listOf(
//            Product("Product 1", "Whiskas", R.drawable.image_product_example, 3.0f, 100),
//            Product("Product 2", "Whiskas", R.drawable.image_product_example, 3.0f, 100),
//            Product("Product 3", "Whiskas", R.drawable.image_product_example, 3.0f, 100),
//            Product("Product 4", "Whiskas", R.drawable.image_product_example, 3.0f, 100),
//            Product("Product 2", "Whiskas", R.drawable.image_product_example, 3.0f, 100)
//        )
//    }
}
