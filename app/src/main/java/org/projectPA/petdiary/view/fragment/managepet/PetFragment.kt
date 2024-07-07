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
    private lateinit var petAdapter: PetAdapter
    private val petViewModel: PetViewModel by navGraphViewModels(R.id.pet_nav) { PetViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Menginisialisasi adapter dan mengatur click listener untuk item dalam daftar hewan peliharaan
        petAdapter = PetAdapter(onClick = { pet, _ ->
            petViewModel.setPet(pet) // Mengatur data hewan peliharaan di ViewModel

            findNavController().navigate(R.id.action_myPetFragment_to_myPetDetailsFragment) // Navigasi ke detail hewan peliharaan
        })

        // Mengatur adapter untuk RecyclerView
        binding.petRV.adapter = petAdapter

        // Mengamati perubahan data hewan peliharaan di ViewModel
        petViewModel.pets.observe(viewLifecycleOwner) { pets ->

            // Mengirimkan daftar hewan peliharaan ke adapter
            petAdapter.submitList(pets)

            // Mengatur jika hewan peliharaan tidak ada
            binding.noPetTV.visibility = if (pets.isEmpty()) View.VISIBLE else View.GONE
            binding.petRV.visibility = if (pets.isEmpty()) View.GONE else View.VISIBLE
        }

        petViewModel.loadPet() // Memuat data hewan peliharan dari ViewModel

        // Tombol tambah data Hewan peliharan
        binding.addPetBtn.setOnClickListener {
            findNavController().navigate(R.id.action_myPetFragment_to_myPetAddFragment)
        }

        // Tombol Back di TopAppBar untuk menutup Activity
        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().finish()
        }
    }
}
