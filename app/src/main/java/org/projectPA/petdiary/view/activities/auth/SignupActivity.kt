package org.projectPA.petdiary.view.activities.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
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

        // Inisialisasi ViewModel untuk aktivitas ini
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        // Listener untuk tombol signup
        binding.signUpBtn.setOnClickListener {
            val name = binding.nameTIET.text.toString()
            val email = binding.emailTIET.text.toString()
            val password = binding.passwordTIET.text.toString()
            val confirmPassword = binding.confirmPasswordTIET.text.toString()

            // Validasi nama
            if (name.isEmpty() || name.length > 100) {
                showSnackbar("Name is required and must be less than 100 characters")
                return@setOnClickListener
            }

            // Validasi email
            if (email.isEmpty() || email.length > 100) {
                showSnackbar("Email is required and must be less than 100 characters")
                return@setOnClickListener
            }

            // Validasi format email
            if (!isValidEmail(email)) {
                showSnackbar("Invalid email address!")
                return@setOnClickListener
            }

            // Validasi kecocokan password
            if (password != confirmPassword) {
                showSnackbar("Passwords do not match!")
                return@setOnClickListener
            }

            // Validasi format password
            if (!isValidPassword(password)) {
                showSnackbar("Password should be between 6 and 12 characters and contain letters, numbers, and optionally dots!")
                return@setOnClickListener
            }

            // Cek apakah nama sudah digunakan
            viewModel.checkIfNameExists(name) { nameExists ->
                if (nameExists) {
                    showSnackbar("Name already taken")
                } else {
                    viewModel.uploadData(name, email, password)
                }
            }
        }

        // Observasi hasil signup
        viewModel.signupSuccess.observe(this, Observer { success ->
            if (success) {
                startActivity(Intent(this, SigninActivity::class.java))
                finish()
            }
        })

        // Observasi error signup
        viewModel.signupError.observe(this, Observer { error ->
            if (error != null) {
                showSnackbar(error)
            }
        })

        // Observasi status loading
        viewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        })

        // Listener untuk teks signin
        binding.signInTextView.setOnClickListener {
            startActivity(Intent(this, SigninActivity::class.java))
            finish()
        }
    }

    // Fungsi untuk menampilkan pesan Snackbar
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

    // Fungsi untuk validasi format email
    private fun isValidEmail(email: String): Boolean {
        val regex = Regex("[a-zA-Z0-9._-]+@(?:gmail|outlook|yahoo|icloud).+[a-z]+")
        return regex.matches(email)
    }

    // Fungsi untuk validasi format password
    private fun isValidPassword(password: String): Boolean {
        val regex = Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d.]{6,12}$")
        return regex.matches(password)
    }
}
