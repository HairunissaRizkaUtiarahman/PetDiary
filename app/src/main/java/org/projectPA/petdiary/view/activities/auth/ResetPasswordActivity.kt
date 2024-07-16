package org.projectPA.petdiary.view.activities.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.projectPA.petdiary.databinding.ActivityResetPasswordBinding

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tombol Reset
        binding.resetBtn.setOnClickListener {
            val email = binding.emailTIET.text.toString()
            val edtEmail = binding.emailTIET

            // Validasi email
            if (email.isEmpty()) {
                edtEmail.error = "Email cannot be empty"
                edtEmail.requestFocus()
                return@setOnClickListener
            }

            // Cek Email Valid
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edtEmail.error = "Email not Valid"
                edtEmail.requestFocus()
                return@setOnClickListener
            }

            // Mengirim Link Reset Password
            validateEmailAndSendResetLink(email)
        }

        // Tombol Sign In
        binding.signInTextView.setOnClickListener {
            startActivity(Intent(this, SigninActivity::class.java))
        }
    }

    // Fungsi validasi email dan mengirim tautan reset password
    private fun validateEmailAndSendResetLink(email: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").whereEqualTo("email", email).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    sendResetPasswordEmail(email)
                } else {
                    showSnackbar("Email not found")
                }
            }
            .addOnFailureListener {
                showSnackbar("Failed to validate email: ${it.message}")
            }
    }

    // Fungsi mengirim email reset password menggunakan Firebase Auth
    private fun sendResetPasswordEmail(email: String) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                showSnackbar("Email to reset password has been sent")
                startActivity(Intent(this, SigninActivity::class.java))
                finish()
            } else {
                showSnackbar("${it.exception?.message}")
            }
        }
    }

    // Pesan snackbar
    private fun showSnackbar(message: String) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        snackbar.show()
    }
}
