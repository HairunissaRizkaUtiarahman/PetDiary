package org.projectPA.petdiary.view.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseUser
import org.projectPA.petdiary.databinding.ActivitySignupBinding
import org.projectPA.petdiary.viewmodel.AuthViewModel

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signUpBtn.setOnClickListener {
            val name = binding.nameTIET.text.toString().trim()
            val address = binding.addressTIET.text.toString().trim()
            val email = binding.emailTIET.text.toString().trim()
            val password = binding.passwordTIET.text.toString().trim()

            if (viewModel.validateInput(name, address, email, password)) {
                viewModel.createUserWithEmailAndPassword(email, password, name, address)
            }
        }

        binding.signInTextView.setOnClickListener {
            startActivity(Intent(this, SigninActivity::class.java))
        }

        viewModel.userCreated.observe(this) { user ->
            user?.let {
                updateUI(it)
            }
        }

        viewModel.errorMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.signUpBtn.isEnabled = !isLoading
        }
    }

    private fun updateUI(user: FirebaseUser) {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}