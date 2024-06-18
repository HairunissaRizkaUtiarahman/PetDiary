package org.projectPA.petdiary.view.fragment.managepet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentPetDetailsBinding
import org.projectPA.petdiary.viewmodel.PetViewModel

class PetDetailsFragment : Fragment() {
    private lateinit var binding: FragmentPetDetailsBinding
    private val viewModel: PetViewModel by navGraphViewModels(R.id.pet_nav)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPetDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe pet data from ViewModel
        viewModel.pet.observe(viewLifecycleOwner) { pet ->
            // Update UI with pet details
            with(binding) {
                petNameTV.text = pet.name
                petTypeTV.text = pet.type
                petGenderTV.text = pet.gender
                petAgeTV.text = requireContext().getString(R.string.age_pet, pet.age)
                petDescTV.text = pet.desc

                // Load pet image using Glide library
                Glide.with(petImageIV.context)
                    .load(pet.imageUrl)
                    .placeholder(R.drawable.image_blank)
                    .into(petImageIV)
            }
        }

        // Handle click on edit button to navigate to edit fragment
        binding.petEditProfileBtn.setOnClickListener {
            Log.d("PetDetailsFragment", "Edit button clicked")
            it.findNavController().navigate(R.id.action_myPetDetailsFragment_to_myPetEditFragment)
        }

        // Handle click on delete button to show confirmation dialog
        binding.deleteBtn.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        // Handle click on navigation icon (back button)
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    // Function to show delete confirmation dialog
    private fun showDeleteConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        val petId = viewModel.pet.value?.id ?: ""
        val imageUrl = viewModel.pet.value?.imageUrl ?: ""

        alertDialogBuilder.apply {
            setMessage("Are you sure you want to delete this pet?")
            setPositiveButton("Yes") { _, _ ->
                // Delete the pet and navigate back
                viewModel.deleteData(petId, imageUrl)
                findNavController().popBackStack()
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        }
        alertDialogBuilder.create().show()
    }
}
