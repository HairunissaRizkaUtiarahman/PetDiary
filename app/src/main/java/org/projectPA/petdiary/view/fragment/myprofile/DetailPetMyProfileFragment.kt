package org.projectPA.petdiary.view.fragment.myprofile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentDetailPetMyProfileBinding
import org.projectPA.petdiary.viewmodel.PetMyProfileViewModel

class DetailPetMyProfileFragment : Fragment() {
    private lateinit var binding: FragmentDetailPetMyProfileBinding

    private val viewModel: PetMyProfileViewModel by navGraphViewModels(R.id.my_profile_nav) { PetMyProfileViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailPetMyProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Mengamati perubahan data pada ViewModel untuk hewan peliharaan (pet)
        viewModel.pet.observe(viewLifecycleOwner) {
            with(binding) {
                petNameTV.text = it.name
                petTypeTV.text = it.type
                petGenderTV.text = it.gender
                petAgeTV.text = requireContext().getString(R.string.age_pet, it.age)
                petDescTV.text = it.desc

                Glide.with(petImageIV.context).load(it.imageUrl).placeholder(R.drawable.image_blank)
                    .into(petImageIV)
            }
        }

        // Tombol Back di TopAppBar untuk kembali ke stack sebelumnya
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}