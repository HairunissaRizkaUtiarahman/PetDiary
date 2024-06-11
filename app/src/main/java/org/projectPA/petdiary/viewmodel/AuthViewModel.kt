package org.projectPA.petdiary.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

open class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val _signupSuccess = MutableLiveData<Boolean>()
    val signupSuccess: LiveData<Boolean> = _signupSuccess

    private val _signupError = MutableLiveData<String>()
    val signupError: LiveData<String> = _signupError

    fun signup(name: String, email: String, password: String, confirmPassword: String) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            _signupError.value = "All fields are required!"
            return
        }
        if (password != confirmPassword) {
            _signupError.value = "Passwords do not match!"
            return
        }
        if (!isValidEmail(email)) {
            _signupError.value = "Invalid email address!"
            return
        }
        if (!isValidPassword(password)) {
            _signupError.value = "Password should be between 6 and 12 characters and contain letters, numbers, and optionally dots!"
            return
        }
        if (name.length > 100) {
            _signupError.value = "Name should not exceed 100 characters!"
            return
        }

        val auth = FirebaseAuth.getInstance()
        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val signInMethods = task.result?.signInMethods ?: emptyList()
                if (signInMethods.isNotEmpty()) {
                    _signupError.value = "Account already registered!"
                } else {
                    checkIfNameExists(auth, name, email, password)
                }
            } else {
                _signupError.value = "Failed to check email registration: ${task.exception?.message}"
            }
        }
    }

    private fun checkIfNameExists(auth: FirebaseAuth, name: String, email: String, password: String) {
        val db = FirebaseFirestore.getInstance()
        val lowercaseName = name.toLowerCase(Locale.ROOT)
        db.collection("user").whereEqualTo("lowercaseName", lowercaseName).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val documents = task.result?.documents ?: emptyList()
                if (documents.isNotEmpty()) {
                    _signupError.value = "Name already taken!"
                } else {
                    createUser(auth, name, lowercaseName, email, password)
                }
            } else {
                _signupError.value = "Failed to check name: ${task.exception?.message}"
            }
        }
    }

    private fun createUser(auth: FirebaseAuth, name: String, lowercaseName: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid
                auth.currentUser?.sendEmailVerification()
                if (userId != null) {
                    val user = hashMapOf(
                        "name" to name,
                        "lowercaseName" to lowercaseName,
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
                _signupError.value = "Registration failed: ${task.exception?.message}"
            }
        }
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
