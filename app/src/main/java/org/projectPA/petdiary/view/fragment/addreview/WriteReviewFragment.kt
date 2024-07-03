package org.projectPA.petdiary.view.fragment.addreview

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentWriteReviewBinding
import org.projectPA.petdiary.viewmodel.GiveReviewViewModel


class WriteReviewFragment : Fragment() {

    private lateinit var binding: FragmentWriteReviewBinding
    private lateinit var viewModel: GiveReviewViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWriteReviewBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(GiveReviewViewModel::class.java)


        binding.nextButtonToRecommendProduct.isEnabled = false

        binding.prevButtonToUsageProduct.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.frame_layout, UsageProductFragment())
                addToBackStack(null)
            }
        }

        binding.reviewEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val reviewText = s.toString()
                val wordCount = reviewText.trim().split("\\s+".toRegex()).size
                binding.nextButtonToRecommendProduct.isEnabled = wordCount >= 10
                Log.d("WriteReviewFragment", "Review text: $reviewText (word count: $wordCount)")
            }
        })

        binding.nextButtonToRecommendProduct.setOnClickListener {
            val reviewText = binding.reviewEditText.text.toString()
            viewModel.updateReviewText(reviewText)
            parentFragmentManager.commit {
                replace(R.id.frame_layout, RecommendProductFragment())
                addToBackStack(null)
            }
            Log.d("WriteReviewFragment", "Review text set to $reviewText")
        }

        return binding.root
    }
}
