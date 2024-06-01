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
import com.bumptech.glide.Glide
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentPetEditBinding
import org.projectPA.petdiary.viewmodel.PetViewModel


class PetEditFragment : Fragment() {
    private lateinit var binding: FragmentPetEditBinding

    private val viewModel: PetViewModel by navGraphViewModels(R.id.pet_nav)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPetEditBinding.inflate(layoutInflater, container, false)
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

        binding.saveBtn.setOnClickListener {
            val radioGroupCheck = binding.petGenderRG.checkedRadioButtonId
            val checkRadioBtn = view.findViewById<RadioButton>(radioGroupCheck)

            val name = binding.petNameTIET.text.toString().trim()
            val type = binding.petTypeTIET.text.toString().trim()
            val gender = checkRadioBtn.text.toString()
            val age = binding.petAgeTIET.text.toString().trim()
            val desc = binding.petDescTIET.text.toString().trim()

            if (name != "" && type != "" && gender != "" && age != "" && desc != "") {
                Toast.makeText(requireContext(), "Success Update My Pet", Toast.LENGTH_SHORT).show()
                checkRadioBtn.isChecked = false

                val myPetId = viewModel.pet.value?.id ?: ""

                viewModel.updateData(myPetId, name, type, gender, age.toInt(), desc, uri)

                binding.petNameTIET.text?.clear()
                binding.petTypeTIET.text?.clear()
                binding.petAgeTIET.text?.clear()
                binding.petDescTIET.text?.clear()
            } else {
                Toast.makeText(requireContext(), "Failed to Update My Pet", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.saveBtn.text = "UPDATING..."
            } else {
                binding.saveBtn.text = "SAVE"
                findNavController().popBackStack()
            }
        }

        viewModel.pet.observe(viewLifecycleOwner) {
            with(binding) {
                if (it.gender == "Male") {
                    maleRB.isChecked = true
                } else if (it.gender == "Female") {
                    femaleRB.isChecked = true
                }

                petNameTIET.setText(it.name)
                petTypeTIET.setText(it.type)
                petAgeTIET.setText(it.age.toString())
                petDescTIET.setText(it.desc)

                Glide.with(petImageIV.context).load(it.imageUrl).placeholder(R.drawable.image_blank)
                    .into(petImageIV)
            }
        }

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}