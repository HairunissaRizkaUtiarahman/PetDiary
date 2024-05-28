package org.projectPA.petdiary.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    val isLoading = MutableLiveData<Boolean>()
    val userCreated = MutableLiveData<FirebaseUser?>()
    val errorMessage = MutableLiveData<String?>()

    fun validateInput(name: String, address: String, email: String, password: String): Boolean {
        if (name.isEmpty() || address.isEmpty() || email.isEmpty() || password.isEmpty()) {
            errorMessage.value = "All fields are required!"
            return false
        }

        if (name.length > 50) {
            errorMessage.value = "Name must be less than 50 characters!"
            return false
        }

        if (address.length > 100) {
            errorMessage.value = "Address must be less than 100 characters!"
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage.value = "Invalid email format!"
            return false
        }

        return true
    }

    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        name: String,
        address: String
    ) {
        isLoading.value = true
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
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
                                Toast.makeText(
                                    getApplication(),
                                    "Successfully Added!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                userCreated.value = currentUser
                            }
                            .addOnFailureListener { e ->
                                errorMessage.value = "Failed! ${e.message}"
                            }
                    } else {
                        errorMessage.value = "No user logged in!"
                    }
                } else {
                    errorMessage.value = "Auth Failed! ${task.exception?.message}"
                }
                isLoading.value = false
            }
    }
}