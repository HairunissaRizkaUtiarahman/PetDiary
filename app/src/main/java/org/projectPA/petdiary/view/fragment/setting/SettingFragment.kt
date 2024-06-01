package org.projectPA.petdiary.view.fragment.setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {
    private lateinit var binding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.changePasswordBtn.setOnClickListener {
            it.findNavController().navigate(R.id.action_settingFragment_to_changePasswordSettingFragment)
        }

        binding.backBtn.setOnClickListener {
            requireActivity().finish()
        }

    }
}