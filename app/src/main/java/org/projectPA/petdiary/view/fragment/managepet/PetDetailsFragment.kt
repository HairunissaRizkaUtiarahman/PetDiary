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
    ): View? {
        binding = FragmentPetDetailsBinding.inflate(layoutInflater, container, false)
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

        alertDialogBuilder.apply {
            setMessage("Are you sure you want to delete this post?")
            setPositiveButton("Yes") { _, _ ->
                viewModel.deleteData(petId)
                findNavController().popBackStack()
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        }
        alertDialogBuilder.create().show()
    }
}