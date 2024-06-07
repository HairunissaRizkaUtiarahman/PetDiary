package org.projectPA.petdiary.view.fragment.addreview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentUsageProductBinding
import org.projectPA.petdiary.viewmodel.GiveReviewViewModel

// UsageProductFragment.kt

class UsageProductFragment : Fragment() {

    private lateinit var binding: FragmentUsageProductBinding
    private lateinit var viewModel: GiveReviewViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUsageProductBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(GiveReviewViewModel::class.java)

        val usageOptions = resources.getStringArray(R.array.usage_options)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, usageOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.usageDropdown.adapter = adapter

        binding.prevButtonToGiveRating.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.frame_layout, GiveRatingFragment())
                addToBackStack(null)
            }
        }

        binding.nextButtonToWriteReview.setOnClickListener {
            val usagePeriod = binding.usageDropdown.selectedItem.toString()
            viewModel.updateUsagePeriod(usagePeriod)
            parentFragmentManager.commit {
                replace(R.id.frame_layout, WriteReviewFragment())
                addToBackStack(null)
            }
        }

        return binding.root
    }
}
