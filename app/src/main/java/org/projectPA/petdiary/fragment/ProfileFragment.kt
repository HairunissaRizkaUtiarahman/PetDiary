package org.projectPA.petdiary.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.projectPA.petdiary.R
import org.projectPA.petdiary.ui.activities.MyProfileActivity
import org.projectPA.petdiary.ui.activities.auth.LoginActivity

class ProfileFragment : Fragment() {
    private lateinit var name_Tv: TextView
    private lateinit var bio_Tv: TextView

    private lateinit var profileImage_IV : ImageView

    private var db = FirebaseFirestore.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val logout_Btn: Button = view.findViewById(R.id.logout_Btn)
        val myProfile_Btn: Button = view.findViewById(R.id.myProfile_Btn)

        name_Tv = view.findViewById(R.id.name_Tv)
        bio_Tv = view.findViewById(R.id.bio_Tv)
        profileImage_IV = view.findViewById(R.id.profileImage_IV)

        fetchUserProfileData()

        myProfile_Btn.setOnClickListener {
            val intent = Intent(activity, MyProfileActivity::class.java)
            startActivity(intent)
        }

        logout_Btn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
        return view
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
                        Toast.makeText(requireActivity(), "Document does not exist", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireActivity(), "Failed to retrieve data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}