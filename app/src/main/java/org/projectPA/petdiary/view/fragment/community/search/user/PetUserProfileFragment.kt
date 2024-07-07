package org.projectPA.petdiary.view.fragment.community.search.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentPetUserProfileBinding
import org.projectPA.petdiary.view.adapters.PetUserProfileAdapter
import org.projectPA.petdiary.viewmodel.PetUserProfileViewModel
import org.projectPA.petdiary.viewmodel.UserViewModel

class PetUserProfileFragment : Fragment() {
    private lateinit var binding: FragmentPetUserProfileBinding
    private lateinit var adapter: PetUserProfileAdapter

    private val userViewModel: UserViewModel by navGraphViewModels(R.id.community_nav) { UserViewModel.Factory }
    private val petUserProfileViewModel: PetUserProfileViewModel by navGraphViewModels(R.id.community_nav) { PetUserProfileViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPetUserProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = PetUserProfileAdapter(onClick = { myPet, _ ->
            petUserProfileViewModel.setPet(myPet)
            findNavController().navigate(R.id.action_userProfileFragment_to_detailPetUserProfileFragment)
        })

        binding.petRV.adapter = adapter

        petUserProfileViewModel.pets.observe(viewLifecycleOwner) { pets ->
            adapter.submitList(pets)

            binding.noPetTV.visibility = if (pets.isEmpty()) View.VISIBLE else View.GONE

            binding.petRV.visibility = if (pets.isEmpty()) View.GONE else View.VISIBLE
        }

        userViewModel.user.observe(viewLifecycleOwner) { user ->
            user?.id?.let {
                petUserProfileViewModel.loadData(it)
            }
        }
    }
}