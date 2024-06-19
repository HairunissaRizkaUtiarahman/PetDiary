package org.projectPA.petdiary.view.fragment.managepet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentPetBinding
import org.projectPA.petdiary.view.adapters.PetAdapter
import org.projectPA.petdiary.viewmodel.PetViewModel

class PetFragment : Fragment() {
    private lateinit var binding: FragmentPetBinding
    private lateinit var adapter: PetAdapter
    private val viewModel: PetViewModel by navGraphViewModels(R.id.pet_nav) { PetViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the RecyclerView adapter
        adapter = PetAdapter(onClick = { pet, _ ->
            // Set the selected pet in ViewModel
            viewModel.setPet(pet)

            // Navigate to pet details fragment
            findNavController().navigate(R.id.action_myPetFragment_to_myPetDetailsFragment)
        })

        // Set the adapter to RecyclerView
        binding.petRV.adapter = adapter

        // Observe the pets LiveData from ViewModel
        viewModel.pets.observe(viewLifecycleOwner) { pets ->
            // Submit the list of pets to the adapter
            adapter.submitList(pets)

            // Show or hide the "No Pets" TextView based on the list size
            binding.noPetTV.visibility = if (pets.isEmpty()) View.VISIBLE else View.GONE

            // Show or hide the RecyclerView based on the list size
            binding.petRV.visibility = if (pets.isEmpty()) View.GONE else View.VISIBLE
        }

        // Load pets data from ViewModel
        viewModel.loadPet()

        // Handle add pet button click
        binding.addPetBtn.setOnClickListener {
            findNavController().navigate(R.id.action_myPetFragment_to_myPetAddFragment)
        }

        // Handle navigation icon click (back button)
        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().finish()
        }
    }
}
