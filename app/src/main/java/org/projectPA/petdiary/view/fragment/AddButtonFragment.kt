package org.projectPA.petdiary.view.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.projectPA.petdiary.databinding.FragmentAddButtonBinding
import org.projectPA.petdiary.view.activities.ChoosePetCategoryActivity

class AddButtonFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAddButtonBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddButtonBinding.inflate(inflater, container, false)
        binding.addAProductButton.setOnClickListener {
            Log.d("FragmentAddButton", "add_a_product_button clicked")
            startActivity(Intent(activity, ChoosePetCategoryActivity::class.java))
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addAProductButton.setOnClickListener {
            Log.d("FragmentAddButton", "add_a_product_button clicked")
            startActivity(Intent(activity, ChoosePetCategoryActivity::class.java))
        }
    }
}