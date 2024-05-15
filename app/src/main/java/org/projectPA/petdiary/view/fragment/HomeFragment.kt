package org.projectPA.petdiary.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.projectPA.petdiary.databinding.FragmentHomeBinding
import org.projectPA.petdiary.view.activities.ArticleHomePageActivity
import org.projectPA.petdiary.view.activities.CommunityHomePageActivity
import org.projectPA.petdiary.view.activities.FindPetShopVetActivity
import org.projectPA.petdiary.view.activities.MyPetActivity
import org.projectPA.petdiary.view.activities.ReviewHomePageActivity

//import org.projectPA.petdiary.ui.adapters.ProductAdapter

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
//    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        adapter = ProductAdapter(requireContext(), getProductList())
//        binding.recyclerViewHorizontal.apply {
//            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//            adapter = this@HomeFragment.adapter
//        }

        // Set listener for each button
        binding.managePetButton.setOnClickListener {
            startActivity(Intent(activity, MyPetActivity::class.java))
        }

        binding.reviewButton.setOnClickListener {
            startActivity(Intent(activity, ReviewHomePageActivity::class.java))
        }

        binding.findPetshopClinicButton.setOnClickListener {
            startActivity(Intent(activity, FindPetShopVetActivity::class.java))
        }

        binding.communityButton.setOnClickListener {
            startActivity(Intent(activity, CommunityHomePageActivity::class.java))
        }

        binding.articleButton.setOnClickListener {
            startActivity(Intent(activity, ArticleHomePageActivity::class.java))
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