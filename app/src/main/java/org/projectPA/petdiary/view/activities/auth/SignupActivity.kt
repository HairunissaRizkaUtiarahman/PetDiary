package org.projectPA.petdiary.view.activities.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import org.projectPA.petdiary.SnackbarIdlingResource
import org.projectPA.petdiary.databinding.ActivitySignupBinding
import org.projectPA.petdiary.viewmodel.AuthViewModel

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        binding.signUpBtn.setOnClickListener {
            val name = binding.nameTIET.text.toString()
            val email = binding.emailTIET.text.toString()
            val password = binding.passwordTIET.text.toString()
            val confirmPassword = binding.confirmPasswordTIET.text.toString()

            // Validate inputs
            if (name.isEmpty() || name.length > 100) {
                Toast.makeText(
                    this,
                    "Name is required and must be less than 100 characters",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (email.isEmpty() || email.length > 100) {
                Toast.makeText(
                    this,
                    "Email is required and must be less than 100 characters",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (!isValidEmail(email)) {
                Toast.makeText(
                    this,
                    "Invalid email address!",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidPassword(password)) {
                Toast.makeText(
                    this,
                    "Password should be between 6 and 12 characters and contain letters, numbers, and optionally dots!",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            viewModel.checkIfNameExists(name) { nameExists ->
                if (nameExists) {
                    Toast.makeText(this, "Name already taken", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.uploadData(name, email, password)
                }
            }
        }

        viewModel.signupSuccess.observe(this, Observer { success ->
            if (success) {
                showSnackbar("Registration successful! Please verify your email before logging in.")
                startActivity(Intent(this, SigninActivity::class.java))
                finish()
            }
        })

        viewModel.signupError.observe(this, Observer { error ->
            if (error != null) {
                showSnackbar(error)
            }
        })

        viewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        })

        binding.signInTextView.setOnClickListener {
            startActivity(Intent(this, SigninActivity::class.java))
            finish()
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

    private fun isValidEmail(email: String): Boolean {
        val regex = Regex("[a-zA-Z0-9._-]+@(?:gmail|outlook|yahoo|icloud).+[a-z]+")
        return regex.matches(email)
    }

    private fun isValidPassword(password: String): Boolean {
        val regex = Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d.]{6,12}$")
        return regex.matches(password)
    }
}
