package org.projectPA.petdiary.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.projectPA.petdiary.databinding.FragmentAddButtonBinding
import org.projectPA.petdiary.ui.activities.ChoosePetCategoryActivity

class FragmentAddButton : Fragment() {

    private var _binding: FragmentAddButtonBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddButtonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addAProductButton.setOnClickListener {
            startActivity(Intent(activity, ChoosePetCategoryActivity::class.java))
        }
    }
}

