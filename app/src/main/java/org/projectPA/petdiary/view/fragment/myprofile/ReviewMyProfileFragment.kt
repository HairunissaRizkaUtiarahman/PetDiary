package org.projectPA.petdiary.view.fragment.myprofile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch
import org.projectPA.petdiary.databinding.FragmentReviewMyProfileBinding
import org.projectPA.petdiary.model.Product
import org.projectPA.petdiary.model.Review
import org.projectPA.petdiary.model.ReviewWithProduct
import org.projectPA.petdiary.view.adapters.ReviewWithProductAdapter
import org.projectPA.petdiary.viewmodel.ReviewViewModel

class ReviewMyProfileFragment : Fragment() {

    private lateinit var binding: FragmentReviewMyProfileBinding
    private val viewModel: ReviewViewModel by viewModels()
    private lateinit var reviewWithProductAdapter: ReviewWithProductAdapter
    private val db = FirebaseFirestore.getInstance()

    private val userId: String by lazy {
        FirebaseAuth.getInstance().currentUser?.uid ?: "unknown_user"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReviewMyProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeReviews()

        // Fetch reviews for the current user
        viewModel.fetchReviewsByUserId(userId)
    }

    private fun setupRecyclerView() {
        reviewWithProductAdapter = ReviewWithProductAdapter(emptyList(), requireContext())
        binding.myReviewList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = reviewWithProductAdapter
        }
    }

    private fun observeReviews() {
        viewModel.reviews.observe(viewLifecycleOwner) { reviews ->
            reviews?.let {
                // Fetch product details and combine with reviews
                lifecycleScope.launch {
                    val combinedData = combineReviewsWithProducts(it)
                    reviewWithProductAdapter.updateData(combinedData)
                }
            }
        }
    }

    private suspend fun combineReviewsWithProducts(reviews: List<Review>): List<ReviewWithProduct> {
        val combinedList = mutableListOf<ReviewWithProduct>()
        for (review in reviews) {
            val productSnapshot = db.collection("products").document(review.productId).get().await()
            val product = productSnapshot.toObject(Product::class.java)
            if (product != null) {
                combinedList.add(ReviewWithProduct(review, product))
            } else {
                Log.d("ReviewMyProfileFragment", "Product not found for productId: ${review.productId}")
            }
        }
        Log.d("ReviewMyProfileFragment", "Combined data size: ${combinedList.size}")
        return combinedList
    }
}
