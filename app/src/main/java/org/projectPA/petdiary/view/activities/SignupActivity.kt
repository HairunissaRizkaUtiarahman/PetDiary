package org.projectPA.petdiary.view.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import org.projectPA.petdiary.databinding.ActivitySignupBinding
import org.projectPA.petdiary.viewmodel.AuthViewModel

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var viewModel: AuthViewModel

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
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                finish()
            }
        })

        viewModel.signupError.observe(this, Observer { error ->
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        })

        binding.signInTextView.setOnClickListener {
            startActivity(Intent(this, SigninActivity::class.java))
            finish()
        }
    }
}