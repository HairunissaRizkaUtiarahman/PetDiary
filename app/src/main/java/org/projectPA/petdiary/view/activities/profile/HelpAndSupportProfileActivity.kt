package org.projectPA.petdiary.view.activities.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.projectPA.petdiary.databinding.ActivityHelpAndSupportProfileBinding

class HelpAndSupportProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHelpAndSupportProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpAndSupportProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        val user = auth.currentUser

        binding.openEmailBtn.setOnClickListener {
            user?.let {
                val userId = it.uid
                getUserName(userId) { userName ->
                    if (userName != null) {
                        val subject = "Butuh bantuan dari akun $userName"
                        val body = """
                            Jenis bantuan :
                            
                            Deskripsi :
                        """.trimIndent()
                        sendEmail("akupetdiary@gmail.com", subject, body)
                    } else {
                        Toast.makeText(this, "Failed to retrieve user name", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } ?: run {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }
    }


    private fun getUserName(userId: String, callback: (String?) -> Unit) {
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val userName = document.getString("name")
                    callback(userName)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }

    }

    private fun sendEmail(email: String, subject: String, body: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        try {
            startActivity(Intent.createChooser(intent, "Choose an email client"))
        } catch (ex: android.content.ActivityNotFoundException) {
            Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show()
        }
    }
}
