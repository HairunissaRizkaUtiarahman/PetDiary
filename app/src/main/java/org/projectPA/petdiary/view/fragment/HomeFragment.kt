package org.projectPA.petdiary.view.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentHomeBinding
import org.projectPA.petdiary.model.Product
import org.projectPA.petdiary.view.activities.CommunityHomePageActivity
import org.projectPA.petdiary.view.activities.MyPetActivity
import org.projectPA.petdiary.view.activities.ProductDetailActivity
import org.projectPA.petdiary.view.activities.ReviewHomePageActivity
import org.projectPA.petdiary.view.adapters.ProductAdapter
import org.projectPA.petdiary.viewmodel.MyProfileViewModel

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var productAdapter: ProductAdapter
    private lateinit var firestore: FirebaseFirestore

    private val viewModel: MyProfileViewModel by viewModels { MyProfileViewModel.Factory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        setupRecyclerView()
        loadDataFromFirestore()

        binding.managePetButton.setOnClickListener {
            startActivity(Intent(activity, MyPetActivity::class.java))
        }

        binding.reviewButton.setOnClickListener {
            startActivity(Intent(activity, ReviewHomePageActivity::class.java))
        }

        binding.communityButton.setOnClickListener {
            startActivity(Intent(activity, CommunityHomePageActivity::class.java))
        }
        fetchData()
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(emptyList()) { productId ->
            val intent = Intent(context, ProductDetailActivity::class.java).apply {
                putExtra("productId", productId)
            }
            startActivity(intent)
        }
        binding.listMostReviewProduct.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.listMostReviewProduct.adapter = productAdapter
    }

    private fun loadDataFromFirestore() {
        firestore.collection("products")
            .get()
            .addOnSuccessListener { documents ->
                val productList = documents.mapNotNull { it.toObject(Product::class.java) }
                productAdapter.updateData(productList) // Update adapter with fetched products
            }
            .addOnFailureListener { exception ->
                Log.e("HomeFragment", "Error getting documents: ", exception)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchData() {
        viewModel.loadData()

        viewModel.myProfile.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.toolbarReview.title = String.format(getString(R.string.home_name_user), user.name)
            }
        }
    }
}
