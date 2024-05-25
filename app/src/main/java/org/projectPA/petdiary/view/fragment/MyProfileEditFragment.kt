package org.projectPA.petdiary.view.fragment

import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentMyProfileEditBinding
import org.projectPA.petdiary.viewmodel.MyProfileViewModel

class MyProfileEditFragment : Fragment() {
    private lateinit var binding: FragmentMyProfileEditBinding

    private val viewModel: MyProfileViewModel by viewModels { MyProfileViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyProfileEditBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        var uri: Uri? = null

        val petImage = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) {
            binding.profileIV.setImageURI(it)
            if (it != null) {
                uri = it
            }
        }

        binding.pickBtn.setOnClickListener {
            petImage.launch("image/*")
        }

        binding.saveBtn.setOnClickListener {
            with(binding) {
                val name = nameTIET.text.toString().trim()
                val address = addressTIET.text.toString().trim()
                val bio = bioTIET.text.toString().trim()

                if (name.isNotEmpty() && address.isNotEmpty() && bio.isNotEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Success Update My Profile",
                        Toast.LENGTH_SHORT
                    ).show()

                    viewModel.updateData(name, address, bio, uri)
                } else {
                    when {
                        name.isEmpty() -> Toast.makeText(
                            requireContext(),
                            "Name cannot be empty",
                            Toast.LENGTH_SHORT
                        ).show()

                        address.isEmpty() -> Toast.makeText(
                            requireContext(),
                            "Address cannot be empty",
                            Toast.LENGTH_SHORT
                        ).show()

                        bio.isEmpty() -> Toast.makeText(
                            requireContext(),
                            "Bio cannot be empty",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
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
        viewModel.myProfile.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.nameTIET.setText(it.name)
                binding.emailTIET.setText(it.email)
                binding.addressTIET.setText(it.address)
                binding.bioTIET.setText(it.bio)

                Glide.with(binding.profileIV.context)
                    .load(it.imageUrl)
                    .placeholder(R.drawable.image_profile)
                    .into(binding.profileIV)
            }
        }

        val inputFilterName = InputFilter.LengthFilter(100)
        val inputFilterEmail = InputFilter.LengthFilter(100)
        val inputFilterAddress = InputFilter.LengthFilter(150)
        val inputFilterBio = InputFilter.LengthFilter(100)

        binding.nameTIET.filters = arrayOf(inputFilterName)
        binding.emailTIET.filters = arrayOf(inputFilterEmail)
        binding.addressTIET.filters = arrayOf(inputFilterAddress)
        binding.bioTIET.filters = arrayOf(inputFilterBio)
    }
}