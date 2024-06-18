package org.projectPA.petdiary.view.fragment.community.search.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentDetailReviewUserProfileBinding
import org.projectPA.petdiary.view.adapters.CommentReviewUserProfileAdapter
import org.projectPA.petdiary.viewmodel.ReviewUserProfileViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class DetailReviewUserProfileFragment : Fragment() {
    private lateinit var binding: FragmentDetailReviewUserProfileBinding
    private lateinit var adapter: CommentReviewUserProfileAdapter

    // Initialize ViewModel using navGraphViewModels
    private val viewModel: ReviewUserProfileViewModel by navGraphViewModels(R.id.community_nav) { ReviewUserProfileViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailReviewUserProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Observe the review LiveData and update the UI
        viewModel.review.observe(viewLifecycleOwner) {
            with(binding) {
                descReviewTV.text = it.reviewText
                brandNameTV.text = it.product?.brandName
                productNameTV.text = it.product?.productName
                productTypeTV.text = it.product?.petType
                reviewDateTV.text =
                    it.reviewDate?.toDate()?.let { date ->
                        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date)
                    }
                usagePeriodReviewTV.text = it.usagePeriod
                recomendedOrNotText.text =
                    if (it.rating >= 4) "I Recommend This Product" else "Not Recommended"
                ratingBar2.rating = it.rating
                nameTV.text = it.user?.name

                // Load product and user images using Glide
                Glide.with(productImageIV.context).load(it.product?.imageUrl)
                    .placeholder(R.drawable.image_blank).into(productImageIV)
                Glide.with(profileImageIV.context).load(it.user?.imageUrl)
                    .placeholder(R.drawable.image_blank).into(profileImageIV)
            }
        }

        // Initialize adapter and set it to RecyclerView
        adapter = CommentReviewUserProfileAdapter()
        binding.commentsRV.adapter = adapter

        // Observe the comments LiveData and update the adapter
        viewModel.commentsReview.observe(viewLifecycleOwner) { comments ->
            adapter.submitList(comments)
        }

        // Load comments for the current review
        viewModel.loadComment(viewModel.review.value?.id ?: "")

        // Set click listener for the send button
        binding.sendBtn.setOnClickListener {
            val comment = binding.commentTIET.text.toString().trim()

            if (comment.isNotEmpty()) {
                viewModel.uploadComment(viewModel.review.value?.id ?: "", comment)
                Toast.makeText(requireContext(), "Success send comment", Toast.LENGTH_SHORT).show()
                binding.commentTIET.text?.clear()
            }
        }

        // Set navigation click listener for the top app bar
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}
