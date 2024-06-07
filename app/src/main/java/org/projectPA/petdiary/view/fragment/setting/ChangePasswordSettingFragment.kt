package org.projectPA.petdiary.view.fragment.setting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import org.projectPA.petdiary.databinding.FragmentChangePasswordSettingBinding
import org.projectPA.petdiary.view.activities.auth.SigninActivity

class ChangePasswordSettingFragment : Fragment() {
    private lateinit var binding: FragmentChangePasswordSettingBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangePasswordSettingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        auth = FirebaseAuth.getInstance()

        binding.changePasswordBtn.setOnClickListener {
            val oldPassword = binding.oldPasswordTIET.text.toString().trim()
            val newPassword = binding.newPasswordTIET.text.toString().trim()
            val confirmNewPassword = binding.confirmNewPasswordTIET.text.toString().trim()

            val passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d.]{6,12}$"
            if (!newPassword.matches(passwordRegex.toRegex())) {
                Toast.makeText(
                    requireContext(),
                    "Password must be 4-8 characters long and contain both letters and numbers",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (newPassword != confirmNewPassword) {
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val user = auth.currentUser
            if (user != null && user.email != null) {
                val credential = EmailAuthProvider.getCredential(user.email!!, oldPassword)

                user.reauthenticate(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                // Logout user after password change
                                auth.signOut()
                                val intent = Intent(requireContext(), SigninActivity::class.java)
                                startActivity(intent)
                                Toast.makeText(
                                    requireContext(),
                                    "Password changed successfully, Please login again",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // Navigate to login screen or perform necessary action after logout
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Failed to change password",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Authentication failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}