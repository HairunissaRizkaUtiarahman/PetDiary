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


            if (name.isEmpty() || name.length > 100) {
                showSnackbar("Name is required and must be less than 100 characters")
                return@setOnClickListener
            }

            if (email.isEmpty() || email.length > 100) {
                showSnackbar("Email is required and must be less than 100 characters")
                return@setOnClickListener
            }

            if (!isValidEmail(email)) {
                showSnackbar("Invalid email address!")
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                showSnackbar("Passwords do not match!")
                return@setOnClickListener
            }

            if (!isValidPassword(password)) {
                showSnackbar("Password should be between 6 and 12 characters and contain letters, numbers, and optionally dots!")
                return@setOnClickListener
            }

            viewModel.checkIfNameExists(name) { nameExists ->
                if (nameExists) {
                    showSnackbar("Name already taken")
                } else {
                    viewModel.uploadData(name, email, password)
                }
            }
        }

        viewModel.signupSuccess.observe(this, Observer { success ->
            if (success) {
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
