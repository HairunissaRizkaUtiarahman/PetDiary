package org.projectPA.petdiary.view.fragment.community.search.user

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import org.projectPA.petdiary.databinding.FragmentReviewUserProfileBinding
import org.projectPA.petdiary.view.adapters.ReviewWithProductAdapter
import org.projectPA.petdiary.viewmodel.UserViewModel

private const val LOG_TAG = "ReviewUserProfileFragment"

class ReviewUserProfileFragment : Fragment() {

    private lateinit var binding: FragmentReviewUserProfileBinding
    private val userViewModel: UserViewModel by activityViewModels { UserViewModel.Factory }
    private lateinit var reviewWithProductAdapter: ReviewWithProductAdapter

    @SuppressLint("LongLogTag")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReviewUserProfileBinding.inflate(inflater, container, false)
        Log.d(LOG_TAG, "onCreateView: Binding initialized")
        return binding.root
    }

    @SuppressLint("LongLogTag")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(LOG_TAG, "onViewCreated called")

        setupRecyclerView()
        observeReviews()

        userViewModel.user.value?.id?.let { userId ->
            Log.d(LOG_TAG, "Loading user reviews for user ID: $userId")
            userViewModel.loadUserReviews(userId)
        }
    }

    @SuppressLint("LongLogTag")
    private fun setupRecyclerView() {
        reviewWithProductAdapter = ReviewWithProductAdapter(emptyList(), requireContext())
        Log.d(LOG_TAG, "Setting up RecyclerView with adapter: $reviewWithProductAdapter")
        binding.myReviewList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = reviewWithProductAdapter
        }
    }

    @SuppressLint("LongLogTag")
    private fun observeReviews() {
        userViewModel.userReviews.observe(viewLifecycleOwner) { reviews ->
            Log.d(LOG_TAG, "Observed user reviews: $reviews")
            reviews?.let {
                reviewWithProductAdapter.updateData(it)
                Log.d(LOG_TAG, "Updated adapter with reviews: $it")
            }
        }
    }
}