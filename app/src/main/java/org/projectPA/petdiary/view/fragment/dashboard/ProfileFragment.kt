package org.projectPA.petdiary.view.fragment.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentProfileBinding
import org.projectPA.petdiary.view.activities.auth.SigninActivity
import org.projectPA.petdiary.view.activities.myprofile.MyProfileActivity
import org.projectPA.petdiary.view.activities.setting.SettingActivity
import org.projectPA.petdiary.viewmodel.MyProfileViewModel

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: MyProfileViewModel by viewModels { MyProfileViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.window?.statusBarColor  =resources.getColor(R.color.orange_main)

        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.window?.statusBarColor  =resources.getColor(R.color.white)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.myProfileBtn.setOnClickListener {
            val intent = Intent(activity, MyProfileActivity::class.java)
            startActivity(intent)
        }

        binding.settingBtn.setOnClickListener {
            val intent = Intent(activity, SettingActivity::class.java)
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
                binding.bioTv.text = it.bio
                Glide.with(binding.profileImageIV.context)
                    .load(it.imageUrl)
                    .placeholder(R.drawable.image_profile)
                    .into(binding.profileImageIV)
            }
        }
    }
}