package org.projectPA.petdiary.view.fragment.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.projectPA.petdiary.databinding.FragmentSupportSettingBinding


class SupportSettingFragment : Fragment() {
    private lateinit var binding: FragmentSupportSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSupportSettingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.emailSupportBtn.setOnClickListener {
            sendEmail()
        }
    }

    private fun sendEmail() {
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("akupetdiary@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Support Request")
            putExtra(Intent.EXTRA_TEXT, "Hello, I need support with...")
        }

        try {
            startActivity(Intent.createChooser(emailIntent, "Send email using..."))
        } catch (ex: android.content.ActivityNotFoundException) {
            Toast.makeText(requireContext(), "No email client installed", Toast.LENGTH_SHORT).show()
        }
    }
}