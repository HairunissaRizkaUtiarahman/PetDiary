package org.projectPA.petdiary.view.fragment.community.search.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentDetailPetUserProfileBinding
import org.projectPA.petdiary.viewmodel.PetUserProfileViewModel

class DetailPetUserProfileFragment : Fragment() {
    private lateinit var binding: FragmentDetailPetUserProfileBinding

    private val viewModel: PetUserProfileViewModel by navGraphViewModels(R.id.community_nav) { PetUserProfileViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailPetUserProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
    }
}