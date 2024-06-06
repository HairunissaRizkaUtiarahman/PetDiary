package org.projectPA.petdiary.view.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.projectPA.petdiary.databinding.ActivityResetPasswordBinding

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.resetBtn.setOnClickListener {
            val email = binding.emailTIET.text.toString()
            val edtEmail = binding.emailTIET

            if (email.isEmpty()) {
                edtEmail.error = "Email cannot be empty"
                edtEmail.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edtEmail.error = "Email not Valid"
                edtEmail.requestFocus()
                return@setOnClickListener
            }

            validateEmailAndSendResetLink(email)
        }

        binding.signInTextView.setOnClickListener {
            startActivity(Intent(this, SigninActivity::class.java))
        }
    }

    private fun validateEmailAndSendResetLink(email: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("user").whereEqualTo("email", email).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    sendResetPasswordEmail(email)
                } else {
                    Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to validate email: ${it.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun sendResetPasswordEmail(email: String) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(
                    this,
                    "Email to reset password has been sent",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(this, SigninActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
