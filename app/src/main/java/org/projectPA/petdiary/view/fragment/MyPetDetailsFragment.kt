package org.projectPA.petdiary.view.fragment

import android.os.Bundle
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
import org.projectPA.petdiary.databinding.FragmentMyPetDetailsBinding
import org.projectPA.petdiary.viewmodel.MyPetViewModel

class MyPetDetailsFragment : Fragment() {
    private lateinit var binding: FragmentMyPetDetailsBinding
    private val viewModel: MyPetViewModel by navGraphViewModels(R.id.my_pet_nav)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPetDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.petEditProfileBtn.setOnClickListener {
            it.findNavController().navigate(R.id.action_myPetDetailsFragment_to_myPetEditFragment)
        }

        binding.deleteBtn.setOnClickListener {
            showDeleteConfirmationDialog()
        }
        fetchData()

    }

    override fun onResume() {
        super.onResume()
        fetchData()
    }

    private fun fetchData() {
        viewModel.loadData()
        with(binding) {
            petNameTV.text = viewModel.pet.name
            petTypeTV.text = viewModel.pet.type
            petGenderTV.text = viewModel.pet.gender
            petAgeTV.text = viewModel.pet.age.toString()
            petDescTV.text = viewModel.pet.desc

            Glide.with(petImageIV.context).load(viewModel.pet.imageUrl)
                .placeholder(R.drawable.image_blank).into(petImageIV)
        }
    }

    private fun showDeleteConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        val myPetId = viewModel.pet.id ?: ""

        alertDialogBuilder.apply {
            setMessage("Are you sure you want to delete this post?")
            setPositiveButton("Yes") { _, _ ->
                viewModel.deleteData(myPetId)
                findNavController().popBackStack()
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        }
        alertDialogBuilder.create().show()
    }
}