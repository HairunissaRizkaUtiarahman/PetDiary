package org.projectPA.petdiary.view.fragment.managepet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentPetBinding
import org.projectPA.petdiary.view.activities.DashboardActivity
import org.projectPA.petdiary.view.adapters.PetAdapter
import org.projectPA.petdiary.viewmodel.PetViewModel

class PetFragment : Fragment() {
    private lateinit var binding: FragmentPetBinding
    private lateinit var adapter: PetAdapter
    private val viewModel: PetViewModel by navGraphViewModels(R.id.pet_nav) { PetViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = PetAdapter(onClick = { pet, _ ->
            viewModel.setPet(pet)

            findNavController().navigate(R.id.action_myPetFragment_to_myPetDetailsFragment)
        })

        binding.petRV.adapter = adapter

        viewModel.pets.observe(viewLifecycleOwner) { pets ->
            adapter.submitList(pets)
            if (pets.isEmpty() || pets.all { it.isDeleted == true }) {
                binding.noPetTV.visibility = View.VISIBLE
                binding.petRV.visibility = View.GONE
            } else {
                binding.noPetTV.visibility = View.GONE
                binding.petRV.visibility = View.VISIBLE
            }
        }

        viewModel.loadData()

        binding.addPetBtn.setOnClickListener {
            findNavController().navigate(R.id.action_myPetFragment_to_myPetAddFragment)
        }

        binding.backBtn.setOnClickListener {
            val intent = Intent(requireContext(), DashboardActivity::class.java)
            startActivity(intent)
            requireActivity().finish() // Optionally, finish the current activity
        }
    }
}