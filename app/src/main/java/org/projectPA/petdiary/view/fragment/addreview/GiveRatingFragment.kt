package org.projectPA.petdiary.view.fragment.addreview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentGiveRatingBinding
import org.projectPA.petdiary.viewmodel.GiveReviewViewModel

// GiveRatingFragment.kt

class GiveRatingFragment : Fragment() {

    private lateinit var binding: FragmentGiveRatingBinding
    private lateinit var viewModel: GiveReviewViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGiveRatingBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(GiveReviewViewModel::class.java)

        binding.ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            viewModel.updateRating(rating)
            binding.nextButtonToUsageProduct.isEnabled = rating > 0
            Log.d("GiveRatingFragment", "Rating set to $rating")
        }

        binding.nextButtonToUsageProduct.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.frame_layout, UsageProductFragment())
                addToBackStack(null)
            }
        }

        // Initially disable the button
        binding.nextButtonToUsageProduct.isEnabled = false

        return binding.root
    }
}
