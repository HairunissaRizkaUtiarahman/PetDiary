package org.projectPA.petdiary.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import org.projectPA.petdiary.R

class LoginActivity : AppCompatActivity() {
    private lateinit var email_TextInputEditText: TextInputEditText
    private lateinit var password_TextInputEditText: TextInputEditText

    private lateinit var signUp_TextView: TextView
    private lateinit var signIn_Btn: Button

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        auth = Firebase.auth

        signIn_Btn = findViewById(R.id.signIn_Btn)
        email_TextInputEditText = findViewById(R.id.email_TextInputEditText)
        password_TextInputEditText = findViewById(R.id.password_TextInputEditText)

        signIn_Btn.setOnClickListener{
            val sEmail = email_TextInputEditText.text.toString().trim()
            val sPassword = password_TextInputEditText.text.toString().trim()

            auth.signInWithEmailAndPassword(sEmail, sPassword)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = auth.currentUser
                        updateUI()
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
//                        updateUI(null)
                    }
                }

        }

        signUp_TextView = findViewById(R.id.signUp_TextView)
        signUp_TextView.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateUI() {
        val intent = Intent(this, HomePage::class.java)
        startActivity(intent)
        finish()
    }
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            updateUI()
        }
    }
}