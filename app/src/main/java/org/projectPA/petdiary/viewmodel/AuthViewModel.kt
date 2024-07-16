package org.projectPA.petdiary.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.util.Locale

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    // LiveData untuk keberhasilan proses signup
    private val _signupSuccess = MutableLiveData<Boolean>()
    val signupSuccess: LiveData<Boolean> = _signupSuccess

    // LiveData untuk pesan error pada proses signup
    private val _signupError = MutableLiveData<String>()
    val signupError: LiveData<String> = _signupError

    // LiveData untuk status loading
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Query Mendaftarkan Pengguna Baru dan Menyimpan data ke Firestore
    fun uploadData(name: String, email: String, password: String) = viewModelScope.launch {
        _isLoading.postValue(true) // Set status loading menjadi true
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid
                val lowercaseName = name.lowercase(Locale.ROOT)
                auth.currentUser?.sendEmailVerification()
                if (userId != null) {
                    val user = hashMapOf(
                        "name" to name,
                        "lowercaseName" to lowercaseName,
                        "address" to "",
                        "gender" to "",
                        "bio" to "",
                        "email" to email,
                        "postCount" to 0,
                        "reviewCount" to 0,
                        "petCount" to 0,
                        "isModerator" to false
                    )
                    val db = FirebaseFirestore.getInstance()
                    db.collection("users").document(userId).set(user)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                _signupSuccess.value =
                                    true
                            } else {
                                _signupError.value =
                                    "Failed to save user data!"
                            }
                        }
                }
            } else {
                _signupError.value =
                    "Registration failed: ${task.exception?.message}"
            }
        }
        _isLoading.postValue(false)
    }

    // Fungsi cek nama pengguna sudah ada di Firestore
    fun checkIfNameExists(name: String, callback: (Boolean) -> Unit) {
        val lowercaseName = name.lowercase(Locale.ROOT)
        val db = FirebaseFirestore.getInstance()
        db.collection("users").whereEqualTo("lowercaseName", lowercaseName).get()
            .addOnSuccessListener { documents ->
                val nameExists = documents.any()
                callback(nameExists)
            }
            .addOnFailureListener { exception ->
                _signupError.value =
                    "Failed to check name: ${exception.message}"
                callback(false)
            }
    }
}
