package org.projectPA.petdiary.view.activities.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import org.projectPA.petdiary.FirebaseAuthIdlingResource
import org.projectPA.petdiary.SnackbarIdlingResource
import org.projectPA.petdiary.databinding.ActivitySigninBinding
import org.projectPA.petdiary.view.activities.DashboardActivity

class SigninActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySigninBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.signInBtn.setOnClickListener {
            val email = binding.emailTIET.text.toString().trim()
            val password = binding.passwordTIET.text.toString().trim()

            FirebaseAuthIdlingResource.increment()
            signInWithEmailAndPassword(email, password)
        }

        binding.forgotPassTV.setOnClickListener {
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }

        binding.signUpTV.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun signInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                FirebaseAuthIdlingResource.decrement()
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        if (user.isEmailVerified) {
                            updateUI()
                        } else {
                            showSnackbar("Please check your email address to verify before logging in.")
                            auth.signOut()
                        }
                    }
                } else {
                    showSnackbar("Authentication failed")
                }
            }
    }

    private fun updateUI() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null && currentUser.isEmailVerified) {
            updateUI()
        } else if (currentUser != null && !currentUser.isEmailVerified) {
            Toast.makeText(this, "Please verify your email address before logging in.", Toast.LENGTH_SHORT).show()
            auth.signOut()
        }
    }

    private fun showSnackbar(message: String) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        SnackbarIdlingResource.SnackbarManager.registerSnackbar(snackbar)
        snackbar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar, event: Int) {
                SnackbarIdlingResource.SnackbarManager.unregisterSnackbar(snackbar)
            }
        })
        snackbar.show()
    }
}
