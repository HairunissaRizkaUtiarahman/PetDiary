package org.projectPA.petdiary.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.projectPA.petdiary.R

class MyProfileActivity : AppCompatActivity() {
    private lateinit var name_Tv: TextView
    private lateinit var bio_Tv: TextView
    private lateinit var editProfile_Btn: Button
    private lateinit var profileImage_IV: ImageView

    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_profile)

        name_Tv = findViewById(R.id.name_Tv)
        bio_Tv = findViewById(R.id.bio_Tv)
        profileImage_IV = findViewById(R.id.profileImage_IV)
        editProfile_Btn = findViewById(R.id.editProfile_Btn)

        editProfile_Btn.setOnClickListener {
            val intent = Intent(this, MyProfileEditActivity::class.java)
            startActivity(intent)
        }

        fetchUserProfileData()
    }

    override fun onResume() {
        super.onResume()
        fetchUserProfileData()
    }

    private fun fetchUserProfileData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { uid ->
            db.collection("user").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("name")
                        val bio = document.getString("bio")
                        val imageUrl = document.getString("imageUrl")

                        name_Tv.text = name
                        bio_Tv.text = bio
                        Glide.with(this).load(imageUrl).placeholder(R.drawable.image_blank).into(profileImage_IV)
                    } else {
                        Toast.makeText(this, "Document does not exist", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to retrieve data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}