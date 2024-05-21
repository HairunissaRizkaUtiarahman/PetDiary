package org.projectPA.petdiary.view.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.projectPA.petdiary.databinding.FragmentSortButtonProductBinding

class SortButtonProductFragment(private val onSortSelected: (String) -> Unit) : BottomSheetDialogFragment() {

    private var _binding: FragmentSortButtonProductBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSortButtonProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.PopularOptionSort.setOnClickListener {
            onSortSelected("popular")
            dismiss()
        }

        binding.highestRatingOptionSort.setOnClickListener {
            onSortSelected("highest_rating")
            dismiss()
        }

        binding.newestOptionSort.setOnClickListener {
            onSortSelected("newest")
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
