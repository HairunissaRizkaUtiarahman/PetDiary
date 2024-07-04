package org.projectPA.petdiary.view.fragment.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentProfileBinding
import org.projectPA.petdiary.view.activities.auth.SigninActivity
import org.projectPA.petdiary.view.activities.myprofile.MyProfileActivity
import org.projectPA.petdiary.view.activities.profile.ChangeLanguageProfileActivity
import org.projectPA.petdiary.view.activities.profile.ChangePasswordProfileActivity
import org.projectPA.petdiary.view.activities.profile.EditProfileActivity
import org.projectPA.petdiary.view.activities.profile.HelpAndSupportProfileActivity
import org.projectPA.petdiary.view.activities.setting.SettingActivity
import org.projectPA.petdiary.viewmodel.MyProfileViewModel

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: MyProfileViewModel by viewModels { MyProfileViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.myProfileBtn.setOnClickListener {
            val intent = Intent(activity, MyProfileActivity::class.java)
            startActivity(intent)
        }

        binding.editProfileBtn.setOnClickListener {
            val intent = Intent(activity, EditProfileActivity::class.java)
            startActivity(intent)
        }

        binding.changePasswordBtn.setOnClickListener {
            val intent = Intent(activity, ChangePasswordProfileActivity::class.java)
            startActivity(intent)
        }

        binding.changeLanguageBtn.setOnClickListener {
            val intent = Intent(activity, ChangeLanguageProfileActivity::class.java)
            startActivity(intent)
        }

        binding.supportBtn.setOnClickListener {
            val intent = Intent(activity, HelpAndSupportProfileActivity::class.java)
            startActivity(intent)
        }

        binding.logoutBtn.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(requireContext())

            alertDialogBuilder.apply {
                setMessage("Are you sure you logout?")
                setPositiveButton("Yes") { _, _ ->
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(requireContext(), SigninActivity::class.java)
                    startActivity(intent)
                }
                setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
            }
            alertDialogBuilder.create().show()
        }
        fetchData()
    }

    override fun onResume() {
        super.onResume()
        fetchData()
    }

    private fun fetchData() {
        viewModel.loadMyProfile()

        viewModel.myProfile.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.nameTv.text = it.name
                binding.emailTv.text = it.email
                binding.postCountTV.text = it.postCount.toString()
                binding.reviewCountTV.text = it.reviewCount.toString()
                binding.petCountTV.text = it.petCount.toString()
                Glide.with(binding.profileImageIV.context)
                    .load(it.imageUrl)
                    .placeholder(R.drawable.image_profile)
                    .into(binding.profileImageIV)
            }
        }
    }
}
