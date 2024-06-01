package org.projectPA.petdiary.view.fragment.findpetshopvet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentFindPetShopVetBinding
import org.projectPA.petdiary.view.activities.DashboardActivity

class FindPetShopVetFragment : Fragment() {
    private lateinit var binding: FragmentFindPetShopVetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFindPetShopVetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.searchPetShopBtn.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_findPetShopVetFragment_to_findPetshopFragment)
        }

        binding.searchClinicBtn.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_findPetShopVetFragment_to_findVetFragment)
        }

        binding.backBtn.setOnClickListener {
            requireActivity().finish()
        }
    }
}