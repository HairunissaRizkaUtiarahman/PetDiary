package org.projectPA.petdiary.view.fragment.addreview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentRecommendProductBinding
import org.projectPA.petdiary.viewmodel.GiveReviewViewModel

// RecommendProductFragment.kt

class RecommendProductFragment : Fragment() {

    private lateinit var binding: FragmentRecommendProductBinding
    private lateinit var viewModel: GiveReviewViewModel
    private var recommend: Boolean? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecommendProductBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(GiveReviewViewModel::class.java)

        binding.icThumbsUpInactive.setOnClickListener {
            recommend = true
            binding.icThumbsUpInactive.setImageResource(R.drawable.ic_thumbs_up_active)
            binding.icThumbsDownInactive.setImageResource(R.drawable.ic_thumbs_down_inactive)
            binding.submitButton.isEnabled = true
        }

        binding.icThumbsDownInactive.setOnClickListener {
            recommend = false
            binding.icThumbsUpInactive.setImageResource(R.drawable.ic_thumbs_up_inactive)
            binding.icThumbsDownInactive.setImageResource(R.drawable.ic_thumbs_down_active)
            binding.submitButton.isEnabled = true
        }

        binding.prevButtonToWriteReview.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.frame_layout, WriteReviewFragment())
                addToBackStack(null)
            }
        }

        binding.submitButton.setOnClickListener {
            val productId = viewModel.product.value?.id ?: ""
            if (productId.isNotEmpty()) {
                viewModel.updateRecommendation(recommend ?: false)
                viewModel.submitReview(requireContext(), productId)
                Toast.makeText(requireContext(), "Review submitted successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Product ID is missing", Toast.LENGTH_SHORT).show()
            }
        }

        binding.submitButton.isEnabled = false

        return binding.root
    }
}
