package org.projectPA.petdiary.view.fragment.managepet

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentPetAddBinding
import org.projectPA.petdiary.viewmodel.PetViewModel

class PetAddFragment : Fragment() {
    private lateinit var binding: FragmentPetAddBinding
    private val viewModel: PetViewModel by navGraphViewModels(R.id.pet_nav)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPetAddBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var uri: Uri? = null

        val petImage = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) {
            binding.petImageIV.setImageURI(it)
            if (it != null) {
                uri = it
            }
        }

        binding.pickBtn.setOnClickListener {
            petImage.launch("image/*")
        }

        binding.addBtn.setOnClickListener {
            val radioGroupCheck = binding.petGenderRG.checkedRadioButtonId
            val checkRadioBtn = view.findViewById<RadioButton>(radioGroupCheck)

            val name = binding.petNameTIET.text.toString().trim()
            val type = binding.petTypeTIET.text.toString().trim()
            val gender = checkRadioBtn.text.toString()
            val age = binding.petAgeTIET.text.toString().trim()
            val desc = binding.petDescTIET.text.toString().trim()

            if (name != "" && type != "" && gender != "" && age != "" && desc != "") {
                Toast.makeText(requireContext(), "Success Add My Pet", Toast.LENGTH_SHORT).show()
                checkRadioBtn.isChecked = false

                viewModel.uploadData(name, type, gender, age.toInt(), desc, uri)

                binding.petNameTIET.text?.clear()
                binding.petTypeTIET.text?.clear()
                binding.petAgeTIET.text?.clear()
                binding.petDescTIET.text?.clear()
            } else {
                Toast.makeText(requireContext(), "Failed to Add My Pet", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.addBtn.text = "ADDING..."
            } else {
                binding.addBtn.text = "ADD"
                findNavController().popBackStack()
            }
        }

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}