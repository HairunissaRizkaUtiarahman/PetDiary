package org.projectPA.petdiary.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import org.projectPA.petdiary.R

class RegisterActivity : AppCompatActivity() {
    private lateinit var email_TextInputEditText: TextInputEditText
    private lateinit var password_TextInputEditText: TextInputEditText

    private lateinit var signUp_Btn: Button
    private lateinit var signIn_TextView: TextView

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        auth = Firebase.auth

        email_TextInputEditText = findViewById(R.id.email_TextInputEditText)
        password_TextInputEditText = findViewById(R.id.password_TextInputEditText)

        signUp_Btn = findViewById(R.id.signUp_Btn)

        signUp_Btn.setOnClickListener {
            val sEmail = email_TextInputEditText.text.toString().trim()
            val sPassword = password_TextInputEditText.text.toString().trim()

            auth.createUserWithEmailAndPassword(sEmail, sPassword)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        Toast.makeText(baseContext, "Auth Failed!", Toast.LENGTH_SHORT).show()
//                        updateUI(null)
                    }
                }
        }
        signIn_TextView = findViewById(R.id.signIn_TextView)
        signIn_TextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)

    }
}