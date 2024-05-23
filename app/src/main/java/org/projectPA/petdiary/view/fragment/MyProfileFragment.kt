package org.projectPA.petdiary.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentMyProfileBinding
import org.projectPA.petdiary.view.adapters.MyProfileAdapter
import org.projectPA.petdiary.viewmodel.MyProfileViewModel

class MyProfileFragment : Fragment() {
    private lateinit var binding: FragmentMyProfileBinding
    private lateinit var adapter: MyProfileAdapter
    private val viewModel: MyProfileViewModel by viewModels { MyProfileViewModel.Factory }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.editMyProfileBtn.setOnClickListener {
            it.findNavController().navigate(R.id.action_myProfileFragment_to_myProfileEditFragment)
        }

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

        adapter = MyProfileAdapter(requireActivity())
        binding.myProfileVP.adapter = adapter

        TabLayoutMediator(binding.myProfileTL, binding.myProfileVP) { tab, position ->
            tab.text = when (position) {
                0 -> "POST"
                1 -> "REVIEW"
                else -> null
            }
        }.attach()

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}