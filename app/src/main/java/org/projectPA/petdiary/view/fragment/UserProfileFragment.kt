package org.projectPA.petdiary.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentUserProfileBinding
import org.projectPA.petdiary.view.adapters.UserProfileTLAdapter
import org.projectPA.petdiary.viewmodel.UserSearchViewModel

class UserProfileFragment : Fragment() {
    private lateinit var binding: FragmentUserProfileBinding
    private lateinit var userProfileTLAdapter: UserProfileTLAdapter

    private val viewModel: UserSearchViewModel by navGraphViewModels(R.id.community_nav)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.user.observe(viewLifecycleOwner) {
            viewModel.getUser(it?.id ?: "")

            Log.d("UserProfileFragment", it.toString())

            with(binding) {
                nameTv.text = it.name
                bioTv.text = it.bio

                Glide.with(profileImageIV.context)
                    .load(it.imageUrl)
                    .placeholder(R.drawable.image_profile)
                    .into(profileImageIV)
            }
        }

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        userProfileTLAdapter = UserProfileTLAdapter(requireActivity())
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