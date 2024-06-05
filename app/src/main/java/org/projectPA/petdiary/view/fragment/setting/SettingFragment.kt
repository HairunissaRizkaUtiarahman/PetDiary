package org.projectPA.petdiary.view.fragment.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

        binding.editProfileBtn.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_settingFragment_to_editProfileSettingFragment)
        }

        binding.changePasswordBtn.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_settingFragment_to_changePasswordSettingFragment)
        }

        binding.changeLanguageBtn.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_settingFragment_to_changeLanguageSettingFragment)
        }

        binding.backBtn.setOnClickListener {
            requireActivity().finish()
        }

    }
}