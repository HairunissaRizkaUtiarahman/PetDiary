package org.projectPA.petdiary.view.fragment.managepet

import android.os.Bundle
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
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.projectPA.petdiary.R
import org.projectPA.petdiary.SnackbarIdlingResource
import org.projectPA.petdiary.databinding.FragmentPetDetailsBinding
import org.projectPA.petdiary.viewmodel.PetViewModel

class PetDetailsFragment : Fragment() {
    private lateinit var binding: FragmentPetDetailsBinding
    private val petViewModel: PetViewModel by navGraphViewModels(R.id.pet_nav)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPetDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mengamati perubahan data hewan peliharaan di ViewModel
        petViewModel.pet.observe(viewLifecycleOwner) { pet ->
            with(binding) {

                // Mengisi tampilan dengan data hewan peliharaan
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

        // Tombol Edit Data Hewan Peliharaan
        binding.petEditProfileBtn.setOnClickListener {
            it.findNavController().navigate(R.id.action_myPetDetailsFragment_to_myPetEditFragment)
        }

        // Tombol Delete Data Hewan Peliharaan
        binding.deleteBtn.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        // Tombol Back di TopAppBar untuk kembali ke stack sebelumnya
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    // Fungsi untuk menampilkan dialog konfirmasi delete Hewan Peliharaan
    private fun showDeleteConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        val petId = petViewModel.pet.value?.id ?: ""
        val imageUrl = petViewModel.pet.value?.imageUrl ?: ""

        alertDialogBuilder.apply {
            setMessage("Are you sure you want to delete this pet?")
            setPositiveButton("Yes") { _, _ ->
                lifecycleScope.launch {
                    val result = petViewModel.deletePet(petId, imageUrl)
                    if (result) {
//                        Toast.makeText(
//                            requireContext(),
//                            "Pet deleted successfully",
//                            Toast.LENGTH_SHORT
//                        ).show()
                        showSnackbar("Pet deleted successfully") //Keperluan Testing

                        findNavController().popBackStack()
                    } else {
                        Toast.makeText(requireContext(), "Failed to delete pet", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        }
        alertDialogBuilder.create().show()
    }

    // Fungsi untuk menampilkan Snackbar dengan pesan (Testing)
    // Pesan Snackbar
    private fun showSnackbar(message: String) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        SnackbarIdlingResource.SnackbarManager.registerSnackbar(snackbar)
        snackbar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar, event: Int) {
                SnackbarIdlingResource.SnackbarManager.unregisterSnackbar(snackbar)
            }
        })
        snackbar.show()
    }
}
