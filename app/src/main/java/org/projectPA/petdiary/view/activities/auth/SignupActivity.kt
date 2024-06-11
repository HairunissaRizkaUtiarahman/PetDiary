package org.projectPA.petdiary.view.activities.auth

import android.content.Intent
import android.os.Bundle
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

            viewModel.signup(name, email, password, confirmPassword)
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

}
