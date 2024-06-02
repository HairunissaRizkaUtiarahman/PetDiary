package org.projectPA.petdiary.view.fragment.community.search.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentReviewUserProfileBinding
import org.projectPA.petdiary.view.adapters.ReviewUserProfileAdapter
import org.projectPA.petdiary.viewmodel.ReviewUserProfileViewModel
import org.projectPA.petdiary.viewmodel.UserViewModel


class ReviewUserProfileFragment : Fragment() {
    private lateinit var binding: FragmentReviewUserProfileBinding
    private lateinit var adapter: ReviewUserProfileAdapter

    private val userViewModel: UserViewModel by navGraphViewModels(R.id.community_nav) { UserViewModel.Factory }
    private val reviewUserProfileViewModel: ReviewUserProfileViewModel by navGraphViewModels(R.id.community_nav) { ReviewUserProfileViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReviewUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = ReviewUserProfileAdapter(onClick = { review, _ ->
            reviewUserProfileViewModel.setReview(review)

            findNavController().navigate(R.id.action_userProfileFragment_to_detailReviewUserProfileFragment)
        })

        binding.reviewRV.adapter = adapter

        reviewUserProfileViewModel.reviews.observe(viewLifecycleOwner) { reviews ->
            adapter.submitList(reviews)
            if (reviews.isEmpty()) {
                binding.noReviewTV.visibility = View.VISIBLE
                binding.reviewRV.visibility = View.GONE
            } else {
                binding.noReviewTV.visibility = View.GONE
                binding.reviewRV.visibility = View.VISIBLE
            }
        }

        userViewModel.user.observe(viewLifecycleOwner) { user ->
            user?.id?.let {
                reviewUserProfileViewModel.loadData(it)
            }
        }
    }

}