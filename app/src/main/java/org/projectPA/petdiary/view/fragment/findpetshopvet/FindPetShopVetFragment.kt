package org.projectPA.petdiary.view.fragment.findpetshopvet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentFindPetShopVetBinding

class FindPetShopVetFragment : Fragment() {
    private lateinit var binding: FragmentFindPetShopVetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFindPetShopVetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Tombol "Search Pet Shop"
        binding.searchPetShopBtn.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_findPetShopVetFragment_to_findPetshopFragment)
        }

        // Tombol "Search Vet"
        binding.searchClinicBtn.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_findPetShopVetFragment_to_findVetFragment)
        }

        // Tombol Back di TopAppBar untuk mengakhiri activity
        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().finish()
        }
    }
}