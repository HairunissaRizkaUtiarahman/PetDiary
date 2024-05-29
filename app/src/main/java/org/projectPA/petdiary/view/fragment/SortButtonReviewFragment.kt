package org.projectPA.petdiary.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.projectPA.petdiary.databinding.FragmentSortButtonReviewBinding

class SortButtonReviewFragment(private val onSortOptionSelected: (String) -> Unit) : BottomSheetDialogFragment() {

    private var _binding: FragmentSortButtonReviewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSortButtonReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.NewestReviewOptionSort.setOnClickListener {
            onSortOptionSelected("newest")
            dismiss()
        }

        binding.OldestReviewRatingOptionSort.setOnClickListener {
            onSortOptionSelected("oldest")
            dismiss()
        }

        binding.HighestRatingOptionSort.setOnClickListener {
            onSortOptionSelected("highest_rating")
            dismiss()
        }

        binding.LowestRatingOptionSort.setOnClickListener {
            onSortOptionSelected("lowest_rating")
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
