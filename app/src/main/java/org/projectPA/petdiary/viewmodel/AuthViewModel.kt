package org.projectPA.petdiary.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val _signupSuccess = MutableLiveData<Boolean>()
    val signupSuccess: LiveData<Boolean> = _signupSuccess

    private val _signupError = MutableLiveData<String>()
    val signupError: LiveData<String> = _signupError

    fun signup(name: String, email: String, password: String, confirmPassword: String) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            _signupError.value = "All fields are required!"
            return
        }
        if (password!= confirmPassword) {
            _signupError.value = "Passwords do not match!"
            return
        }
        if (!isValidEmail(email)) {
            _signupError.value = "Invalid email address!"
            return
        }
        if (!isValidPassword(password)) {
            _signupError.value = "Password should be between 6 and 10 characters and contain letters and numbers!"
            return
        }
        if (name.length > 100) {
            _signupError.value = "Name should not exceed 100 characters!"
            return
        }

        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid
                if (userId!= null) {
                    val user = hashMapOf(
                        "name" to name,
                        "email" to email
                    )
                    val db = FirebaseFirestore.getInstance()
                    db.collection("user").document(userId).set(user).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            _signupSuccess.value = true
                        } else {
                            _signupError.value = "Failed to save user data!"
                        }
                    }
                }
            } else {
                _signupError.value = "Registration failed! Try again."
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val regex = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
        return regex.matches(email)
    }

    private fun isValidPassword(password: String): Boolean {
        val regex = Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,10}$")
        return regex.matches(password)
    }
}