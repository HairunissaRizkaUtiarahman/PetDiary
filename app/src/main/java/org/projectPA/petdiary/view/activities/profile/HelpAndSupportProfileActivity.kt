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


        auth = FirebaseAuth.getInstance() // Menginisialisasi instance FirebaseAuth
        firestore = FirebaseFirestore.getInstance() // Menginisialisasi instance FirebaseFirestore
        val user = auth.currentUser // Mendapatkan pengguna saat ini

        // Tombol "Open Email"
        binding.openEmailBtn.setOnClickListener {
            user?.let {
                val userId = it.uid
                getUserName(userId) { userName ->
                    if (userName != null) {
                        // Membuat subjek email dengan nama pengguna
                        val subject = "Butuh bantuan dari akun $userName"

                        // Membuat isi email
                        val body = """
                            Jenis bantuan :
                            
                            Deskripsi :
                        """.trimIndent()

                        // Memanggil fungsi sendEmail untuk mengirim email
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

        // Tombol Back di TopAppBar untuk mengakahiri activity
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }
    }

    // Fungsi untuk mendapatkan nama pengguna dari Firestore
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

    // Fungsi untuk mengirim email
    private fun sendEmail(email: String, subject: String, body: String) {
        val intent = Intent(Intent.ACTION_SEND).apply { // Membuat intent untuk mengirim email
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
