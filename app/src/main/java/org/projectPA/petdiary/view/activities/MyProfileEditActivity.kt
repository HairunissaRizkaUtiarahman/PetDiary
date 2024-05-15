package org.projectPA.petdiary.view.activities

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import org.projectPA.petdiary.R

class MyProfileEditActivity : AppCompatActivity() {
    private lateinit var name_TextInputEditText : TextInputEditText
    private lateinit var address_TextInputEditText : TextInputEditText
    private lateinit var email_TextInputEditText : TextInputEditText
    private lateinit var bio_TextInputEditText : TextInputEditText

    private lateinit var profile_IV : ImageView

    private lateinit var pick_Btn : Button
    private lateinit var save_Btn : Button

    private var db = Firebase.firestore
    private var storageRef = Firebase.storage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_profile_edit)

        var uri : Uri? = null

        storageRef = FirebaseStorage.getInstance()

        profile_IV = findViewById(R.id.profile_IV)

        name_TextInputEditText = findViewById(R.id.name_TextInputEditText)
        address_TextInputEditText = findViewById(R.id.address_TextInputEditText)
        email_TextInputEditText = findViewById(R.id.email_TextInputEditText)
        bio_TextInputEditText = findViewById(R.id.bio_TextInputEditText)

        pick_Btn = findViewById(R.id.pick_Btn)
        save_Btn = findViewById(R.id.save_Btn)

        val profileImage =  registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                profile_IV.setImageURI(it)
                if (it != null) {
                    uri = it
                }
            })

        pick_Btn.setOnClickListener {
            profileImage.launch("image/*")
        }

        setData()

        save_Btn.setOnClickListener {

            val sName = name_TextInputEditText.text.toString()
            val sAddress = address_TextInputEditText.text.toString()
            val sEmail = email_TextInputEditText.text.toString()
            val sBio = bio_TextInputEditText.text.toString()

            var imageUrl: String = ""
            val userId = FirebaseAuth.getInstance().currentUser!!.uid

            if (uri != null) {
                storageRef.getReference("images").child("pictureProfile").child(System.currentTimeMillis().toString())
                    .putFile(uri!!)
                    .addOnSuccessListener {task ->
                        task.metadata!!.reference!!.downloadUrl
                            .addOnSuccessListener {
                                imageUrl = it.toString()

                                val updateMap = mapOf(
                                    "name" to sName,
                                    "address" to sAddress,
                                    "email" to sEmail,
                                    "bio" to sBio,
                                    "imageUrl" to imageUrl
                                )

                                db.collection("user").document(userId).update(updateMap)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Successfully Edit", Toast.LENGTH_SHORT).show()
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Failed to Edit: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                    }
            } else {
                val updateMap = mapOf(
                    "name" to sName,
                    "address" to sAddress,
                    "email" to sEmail,
                    "bio" to sBio
                )

                db.collection("user").document(userId).update(updateMap)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Successfully Edit", Toast.LENGTH_SHORT).show()
                        finish() // Kembali ke halaman profil setelah berhasil mengedit
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to Edit: ${e.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
            }
        }
    }

    private fun setData() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = db.collection("user").document(userId)
        ref.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val name = document.getString("name") ?: ""
                val address = document.getString("address") ?: ""
                val email = document.getString("email") ?: ""
                val bio = document.getString("bio") ?: ""
                val imageUrl = document.getString("imageUrl")

                name_TextInputEditText.setText(name)
                address_TextInputEditText.setText(address)
                email_TextInputEditText.setText(email)
                bio_TextInputEditText.setText(bio)

                Glide.with(profile_IV.context).load(imageUrl).placeholder(R.drawable.image_blank).into(profile_IV)

            } else {
                Toast.makeText(this, "Document does not exist", Toast.LENGTH_SHORT).show()
            }
        }
            .addOnFailureListener{
                Toast.makeText(this, "Failed to retrieve data", Toast.LENGTH_SHORT).show()
            }
    }
}