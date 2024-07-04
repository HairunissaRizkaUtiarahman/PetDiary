package org.projectPA.petdiary.view.activities.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ActivityChangePasswordProfileBinding
import org.projectPA.petdiary.view.activities.auth.SigninActivity

class ChangePasswordProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordProfileBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.changePasswordBtn.setOnClickListener {
            val oldPassword = binding.oldPasswordTIET.text.toString().trim()
            val newPassword = binding.newPasswordTIET.text.toString().trim()
            val confirmNewPassword = binding.confirmNewPasswordTIET.text.toString().trim()

            val passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d.]{6,12}$"
            if (!newPassword.matches(passwordRegex.toRegex())) {
                Toast.makeText(
                    this,
                    "Password must be 6-12 characters long and contain both letters and numbers",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (newPassword != confirmNewPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT)
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
                                auth.signOut()
                                val intent = Intent(this, SigninActivity::class.java)
                                startActivity(intent)
                                Toast.makeText(
                                    this,
                                    "Password changed successfully, Please login again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this,
                                    "Failed to change password",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Authentication failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }
    }
}
