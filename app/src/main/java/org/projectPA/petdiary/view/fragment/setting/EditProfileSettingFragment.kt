package org.projectPA.petdiary.view.fragment.setting

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.MaterialDatePicker
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentEditProfileSettingBinding
import org.projectPA.petdiary.viewmodel.MyProfileViewModel

class EditProfileSettingFragment : Fragment() {
    private lateinit var binding: FragmentEditProfileSettingBinding
    private val viewModel: MyProfileViewModel by viewModels { MyProfileViewModel.Factory }
    private val CAMERA_PERMISSION_REQUEST_CODE = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProfileSettingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var imageUri: Uri? = null

        val profileImage = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) {
            imageUri = it
            binding.profileIV.setImageURI(it)
        }

        val takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    binding.profileIV.setImageURI(imageUri)
                }
            }

        fun takePicture() {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_REQUEST_CODE
                )
            } else {
                val contentValues = ContentValues().apply {
                    put(
                        MediaStore.Images.Media.DISPLAY_NAME,
                        "image_${System.currentTimeMillis()}.jpg"
                    )
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                }
                imageUri = requireContext().contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )
                imageUri?.let { takePictureLauncher.launch(it) }
            }
        }

        binding.pickBtn.setOnClickListener {
            val options = arrayOf("Take Picture", "Choose from Gallery")
            val builder = android.app.AlertDialog.Builder(requireContext())
            builder.setTitle("Select Image")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> takePicture()
                        1 -> profileImage.launch("image/*")
                    }
                }
            builder.show()
        }

        binding.saveBtn.setOnClickListener {
            val radioGroupCheck = binding.genderRG.checkedRadioButtonId
            val checkRadioBtn = view.findViewById<RadioButton>(radioGroupCheck)

            val name = binding.nameTIET.text.toString().trim()
            val address = binding.addressTIET.text.toString().trim()
            val birthdate = binding.birthdateTIET.text.toString().trim()
            val bio = binding.bioTIET.text.toString().trim()

            if (name.isEmpty() || name.length > 100) {
                Toast.makeText(
                    requireContext(),
                    "Name is required and must be less than 100 characters",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (address.isEmpty() || address.length > 150) {
                Toast.makeText(
                    requireContext(),
                    "Address is required and must be less than 150 characters",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (radioGroupCheck == -1) {
                Toast.makeText(requireContext(), "Gender must be selected", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (birthdate.isEmpty()) {
                Toast.makeText(requireContext(), "Birthdate is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (bio.length > 100) {
                Toast.makeText(
                    requireContext(),
                    "Bio must be less than 100 characters",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val gender = checkRadioBtn.text.toString()

            viewModel.checkIfNameExists(name) { nameExists ->
                if (nameExists) {
                    Toast.makeText(requireContext(), "Name already taken", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    viewModel.updateMyProfile(name, address, gender, birthdate, bio, imageUri)
                    Toast.makeText(
                        requireContext(),
                        "Success Update My Profile",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.birthdateTIL.setEndIconOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select birthdate")
                .build()

            datePicker.show(childFragmentManager, "DATE_PICKER")

            datePicker.addOnPositiveButtonClickListener {
                val selectedDate = datePicker.headerText
                binding.birthdateTIET.setText(selectedDate)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.saveBtn.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.saveBtn.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                findNavController().popBackStack()
            }
        }

        viewModel.loadMyProfile()

        viewModel.myProfile.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.nameTIET.setText(it.name)
                binding.emailTIET.setText(it.email)
                binding.addressTIET.setText(it.address)

                if (it.gender == "Male" || it.gender == "Pria") {
                    binding.maleRB.isChecked = true
                } else if (it.gender == "Female" || it.gender == "Wanita") {
                    binding.femaleRB.isChecked = true
                }

                binding.birthdateTIET.setText((it.birthdate))
                binding.bioTIET.setText(it.bio)

                Glide.with(binding.profileIV.context)
                    .load(it.imageUrl)
                    .placeholder(R.drawable.image_profile)
                    .into(binding.profileIV)
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}
