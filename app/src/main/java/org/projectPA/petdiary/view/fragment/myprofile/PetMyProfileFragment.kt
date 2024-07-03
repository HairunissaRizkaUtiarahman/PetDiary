package org.projectPA.petdiary.view.fragment.myprofile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentPetMyProfileBinding
import org.projectPA.petdiary.view.adapters.PetMyProfileAdapter
import org.projectPA.petdiary.viewmodel.PetMyProfileViewModel

class PetMyProfileFragment : Fragment() {
    private lateinit var binding: FragmentPetMyProfileBinding
    private lateinit var adapter: PetMyProfileAdapter

    private val viewModel: PetMyProfileViewModel by navGraphViewModels(R.id.my_profile_nav) { PetMyProfileViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPetMyProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = PetMyProfileAdapter(onClick = { pet, _ ->
            viewModel.setPet(pet)

            findNavController().navigate(R.id.action_myProfileFragment_to_detailPetMyProfileFragment)
        })

        binding.myPetRV.adapter = adapter

        viewModel.pets.observe(viewLifecycleOwner) { pets ->
            adapter.submitList(pets)

            binding.noPetTV.visibility = if (pets.isEmpty()) View.VISIBLE else View.GONE

            binding.myPetRV.visibility = if (pets.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.loadData()
    }
}