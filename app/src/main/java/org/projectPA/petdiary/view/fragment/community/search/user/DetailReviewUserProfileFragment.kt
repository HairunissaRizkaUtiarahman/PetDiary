package org.projectPA.petdiary.view.fragment.community.search.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentDetailReviewUserProfileBinding
import org.projectPA.petdiary.relativeTime
import org.projectPA.petdiary.view.adapters.CommentReviewMyProfileAdapter
import org.projectPA.petdiary.viewmodel.ReviewUserProfileViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class DetailReviewUserProfileFragment : Fragment() {
    private lateinit var binding: FragmentDetailReviewUserProfileBinding

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
        viewModel.review.observe(viewLifecycleOwner) {
            with(binding) {
                descReviewTV.text = it.reviewText
                brandNameTV.text = it.product?.brandName
                productNameTV.text = it.product?.productName
                productTypeTV.text = it.product?.petType
                reviewDateTV.text =
                    it.reviewDate?.toDate()?.let { it1 ->
                        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(it1)
                    }
                usagePeriodReviewTV.text = it.usagePeriod
                recomendedOrNotText.text =
                    if (it.rating >= 4) "I Recommend This Product" else "Not Recommended"
                ratingBar2.rating = it.rating
                nameTV.text = it.user?.name


                Glide.with(productImageIV.context).load(it.product?.imageUrl)
                    .placeholder(R.drawable.image_blank).into(productImageIV)

                Glide.with(profileImageIV.context).load(it.user?.imageUrl)
                    .placeholder(R.drawable.image_blank).into(profileImageIV)
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}