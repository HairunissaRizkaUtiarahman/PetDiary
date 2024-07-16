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

        // Inisialisasi FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Tombol Sign In
        binding.signInBtn.setOnClickListener {
            val email = binding.emailTIET.text.toString().trim()
            val password = binding.passwordTIET.text.toString().trim()

            FirebaseAuthIdlingResource.increment()
            signInWithEmailAndPassword(email, password)
        }

        // Tombol lupa password
        binding.forgotPassTV.setOnClickListener {
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }

        // Tombol Sign Up
        binding.signUpTV.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    // Fungsi untuk sign in menggunakan email dan password
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

    // Fungsi untuk memperbarui UI setelah berhasil login
    private fun updateUI() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    // Override fungsi onStart untuk mengecek status pengguna saat aplikasi dimulai
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser

        // Jika pengguna sudah login dan emailnya sudah diverifikasi, update UI
        if (currentUser != null && currentUser.isEmailVerified) {
            updateUI()

        // Jika pengguna sudah login tapi emailnya belum diverifikasi, tampilkan pesan dan sign out
        } else if (currentUser != null && !currentUser.isEmailVerified) {
            Toast.makeText(this, "Please verify your email address before logging in.", Toast.LENGTH_SHORT).show()
            auth.signOut()
        }
    }

    // Pesan Snackbar
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
