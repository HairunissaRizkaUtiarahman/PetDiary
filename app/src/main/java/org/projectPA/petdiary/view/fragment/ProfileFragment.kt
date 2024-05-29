package org.projectPA.petdiary.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentProfileBinding
import org.projectPA.petdiary.view.activities.LoginActivity
import org.projectPA.petdiary.view.activities.MyProfileActivity
import org.projectPA.petdiary.viewmodel.MyProfileViewModel

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: MyProfileViewModel by viewModels { MyProfileViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.myProfileBtn.setOnClickListener {
            val intent = Intent(activity, MyProfileActivity::class.java)
            startActivity(intent)
        }

        binding.logoutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
        fetchData()
    }

    override fun onResume() {
        super.onResume()
        fetchData()
    }

    private fun fetchData() {
        viewModel.loadData()

        viewModel.myProfile.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.nameTv.text = it.name
                binding.bioTv.text = it.bio
                Glide.with(binding.profileImageIV.context)
                    .load(it.imageUrl)
                    .placeholder(R.drawable.image_profile)
                    .into(binding.profileImageIV)
            }
        }
    }
}