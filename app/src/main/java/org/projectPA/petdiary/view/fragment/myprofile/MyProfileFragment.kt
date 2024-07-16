package org.projectPA.petdiary.view.fragment.myprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentMyProfileBinding
import org.projectPA.petdiary.view.adapters.MyProfileTLAdapter
import org.projectPA.petdiary.viewmodel.MyProfileViewModel

class MyProfileFragment : Fragment() {
    private lateinit var binding: FragmentMyProfileBinding
    private lateinit var adapter: MyProfileTLAdapter
    private val viewModel: MyProfileViewModel by viewModels { MyProfileViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Memuat data profil pengguna
        loadUserProfile()

        adapter = MyProfileTLAdapter(requireActivity())
        binding.myProfileVP.adapter = adapter

        // Menghubungkan TabLayout dengan ViewPager dan menetapkan judul tab
        TabLayoutMediator(binding.myProfileTL, binding.myProfileVP) { tab, position ->
            tab.text = when (position) {
                0 -> "POST"
                1 -> "REVIEW"
                2 -> "PET"
                else -> null
            }
        }.attach()

        // Tombol Back di TopAppBar untuk kembali ke stack sebelumnya
        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().finish()
        }
    }

    // Fungsi untuk memuat data profil pengguna dari ViewModel
    private fun loadUserProfile() {
        viewModel.loadMyProfile()

        // Memantau perubahan data profil pengguna dan mengupdate tampilan sesuai
        viewModel.myProfile.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.nameTv.text = it.name
                binding.bioTv.text = it.bio
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

    // Memuat ulang data profil pengguna saat fragment dilanjutkan
    override fun onResume() {
        super.onResume()
        loadUserProfile()
    }
}
