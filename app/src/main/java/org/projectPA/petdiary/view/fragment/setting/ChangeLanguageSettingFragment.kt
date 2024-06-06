package com.example.testproject.ui.setting

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.projectPA.petdiary.databinding.FragmentChangeLanguageSettingBinding
import java.util.Locale

class ChangeLanguageSettingFragment : Fragment() {

    private var _binding: FragmentChangeLanguageSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChangeLanguageSettingBinding.inflate(inflater, container, false)
        val view = binding.root

        // Load saved language
        val sharedPreferences =
            requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        val currentLanguage = sharedPreferences.getString("lang", "system") ?: "system"
        setLocale(requireContext(), currentLanguage)  // Apply the saved language
        updateCheckImage(currentLanguage)

        binding.systemBtn.setOnClickListener {
            setLocale(requireContext(), "system")
            showLanguageSelected("System")
            saveLanguageSelection("system")
            updateCheckImage("system")
        }

        binding.englishBtn.setOnClickListener {
            setLocale(requireContext(), "en")
            showLanguageSelected("English")
            saveLanguageSelection("en")
            updateCheckImage("en")
        }

        binding.indonesianBtn.setOnClickListener {
            setLocale(requireContext(), "in")
            showLanguageSelected("Bahasa Indonesia")
            saveLanguageSelection("in")
            updateCheckImage("in")
        }

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setLocale(context: Context, lang: String) {
        val locale: Locale = if (lang == "system") {
            Locale.getDefault() // Get the default system language
        } else {
            Locale(lang)
        }
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        // activity?.recreate() // Re-create activity to apply new language
    }

    private fun showLanguageSelected(language: String) {
        Toast.makeText(requireContext(), "Language changed to $language", Toast.LENGTH_SHORT).show()
    }

    private fun saveLanguageSelection(lang: String) {
        val sharedPreferences =
            requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("lang", lang)
            apply()
        }
    }

    private fun updateCheckImage(lang: String) {
        binding.checkSystemIV.visibility = if (lang == "system") View.VISIBLE else View.GONE
        binding.checkEnglishIV.visibility = if (lang == "en") View.VISIBLE else View.GONE
        binding.checkIndonesianIV.visibility = if (lang == "in") View.VISIBLE else View.GONE
    }
}
