package org.projectPA.petdiary.view.fragment.managepet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
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
        binding = FragmentPetDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.pet.observe(viewLifecycleOwner) { pet ->
            with(binding) {
                petNameTV.text = pet.name
                petTypeTV.text = pet.type
                petGenderTV.text = pet.gender
                petAgeTV.text = requireContext().getString(R.string.age_pet, pet.age)
                petDescTV.text = pet.desc

                Glide.with(petImageIV.context)
                    .load(pet.imageUrl)
                    .placeholder(R.drawable.image_blank)
                    .into(petImageIV)
            }
        }

        binding.petEditProfileBtn.setOnClickListener {
            Log.d("PetDetailsFragment", "Edit button clicked")
            it.findNavController().navigate(R.id.action_myPetDetailsFragment_to_myPetEditFragment)
        }

        binding.deleteBtn.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun showDeleteConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        val petId = viewModel.pet.value?.id ?: ""
        val imageUrl = viewModel.pet.value?.imageUrl ?: ""

        alertDialogBuilder.apply {
            setMessage("Are you sure you want to delete this pet?")
            setPositiveButton("Yes") { _, _ ->
                lifecycleScope.launch {
                    val result = viewModel.deletePet(petId, imageUrl)
                    if (result) {
                        Toast.makeText(requireContext(), "Pet deleted successfully", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    } else {
                        Toast.makeText(requireContext(), "Failed to delete pet", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        }
        alertDialogBuilder.create().show()
    }
}
