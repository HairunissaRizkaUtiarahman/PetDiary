package org.projectPA.petdiary.view.fragment.community.search.user

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentUserProfileBinding
import org.projectPA.petdiary.view.adapters.UserProfileTLAdapter
import org.projectPA.petdiary.viewmodel.UserViewModel

class UserProfileFragment : Fragment() {
    private lateinit var binding: FragmentUserProfileBinding
    private lateinit var userProfileTLAdapter: UserProfileTLAdapter
    private val viewModel: UserViewModel by navGraphViewModels(R.id.community_nav) { UserViewModel.Factory }

    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                with(binding) {
                    nameTv.text = it.name
                    bioTv.text = it.bio
                    Glide.with(profileImageIV.context)
                        .load(it.imageUrl)
                        .placeholder(R.drawable.image_profile)
                        .into(profileImageIV)
                }
            }
        }

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        userProfileTLAdapter = UserProfileTLAdapter(requireActivity(), viewModel)
        binding.myProfileVP.adapter = userProfileTLAdapter

        TabLayoutMediator(binding.myProfileTL, binding.myProfileVP) { tab, position ->
            tab.text = when (position) {
                0 -> "POST"
                1 -> "REVIEW"
                else -> null
            }
        }.attach()
    }
}