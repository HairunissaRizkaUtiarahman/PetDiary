package org.projectPA.petdiary.view.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private lateinit var name: TextInputEditText
    private lateinit var address: TextInputEditText
    private lateinit var email: TextInputEditText
    private lateinit var password: TextInputEditText

    private lateinit var signUpButton: Button
    private lateinit var signInTextView: TextView

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        name = findViewById(R.id.name_TIET)
        address = findViewById(R.id.address_TIET)
        email = findViewById(R.id.email_TIET)
        password = findViewById(R.id.password_TIET)

        signUpButton = findViewById(R.id.signUp_Btn)
        signInTextView = findViewById(R.id.signIn_TextView)

        signUpButton.setOnClickListener {
            val name = name.text.toString().trim()
            val address = address.text.toString().trim()
            val email = email.text.toString().trim()
            val password = password.text.toString().trim()

            if (validateInput(name, address, email, password)) {
                createUserWithEmailAndPassword(email, password, name, address)
            }
        }

        signInTextView.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun validateInput(name: String, address: String, email: String, password: String): Boolean {
        if (name.isEmpty() || address.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (name.length > 50) {
            Toast.makeText(this, "Name must be less than 50 characters!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (address.length > 100) {
            Toast.makeText(this, "Address must be less than 100 characters!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun createUserWithEmailAndPassword(email: String, password: String, name: String, address: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    val userId = currentUser?.uid

                    if (userId != null) {
                        val userMap = hashMapOf(
                            "name" to name,
                            "address" to address,
                            "email" to email
                        )

                        db.collection("user").document(userId).set(userMap)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Successfully Added!", Toast.LENGTH_SHORT).show()
                                clearFields()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed! ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "No user logged in!", Toast.LENGTH_SHORT).show()
                    }

                    updateUI(currentUser)
                } else {
                    Toast.makeText(baseContext, "Auth Failed! ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    private fun clearFields() {
        name.text?.clear()
        address.text?.clear()
        email.text?.clear()
        password.text?.clear()
    }
}