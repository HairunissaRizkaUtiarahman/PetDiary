package org.projectPA.petdiary.view.fragment

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentMyPetEditBinding
import org.projectPA.petdiary.viewmodel.MyPetViewModel


class MyPetEditFragment : Fragment() {
    private lateinit var binding: FragmentMyPetEditBinding

    private val viewModel: MyPetViewModel by navGraphViewModels(R.id.my_pet_nav)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPetEditBinding.inflate(layoutInflater, container, false)
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

                val myPetId = viewModel.pet.id ?: ""

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

        viewModel.loadData()
        with(binding) {
            if (viewModel.pet.gender == "Male") {
                maleRB.isChecked = true
            } else if (viewModel.pet.gender == "Female") {
                femaleRB.isChecked = true
            }

            petNameTIET.setText(viewModel.pet.name)
            petTypeTIET.setText(viewModel.pet.type)
            petAgeTIET.setText(viewModel.pet.age.toString())
            petDescTIET.setText(viewModel.pet.desc)

            Glide.with(petImageIV.context).load(viewModel.pet.imageUrl)
                .placeholder(R.drawable.image_blank)
                .into(petImageIV)
        }
    }
}