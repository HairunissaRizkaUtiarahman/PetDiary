package org.projectPA.petdiary.view.fragment.managepet

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
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentPetAddBinding
import org.projectPA.petdiary.viewmodel.PetViewModel

class PetAddFragment : Fragment() {
    private lateinit var binding: FragmentPetAddBinding
    private val viewModel: PetViewModel by navGraphViewModels(R.id.pet_nav)
    private val CAMERA_PERMISSION_REQUEST_CODE = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPetAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var imageUri: Uri? = null

        // Activity result launcher for picking image from gallery
        val petImage = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) {
            imageUri = it
            binding.petImageIV.setImageURI(it)
        }

        // Activity result launcher for taking picture from camera
        val takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    binding.petImageIV.setImageURI(imageUri)
                }
            }

        // Function to take picture using camera
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

        // Handle click on pick button to select image from gallery or take picture using camera
        binding.pickBtn.setOnClickListener {
            val options = arrayOf("Take Picture", "Choose from Gallery")
            val builder = android.app.AlertDialog.Builder(requireContext())
            builder.setTitle("Select Image")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> takePicture()
                        1 -> petImage.launch("image/*")
                    }
                }
            builder.show()
        }

        // Handle click on add button to add new pet
        binding.addBtn.setOnClickListener {
            val radioGroupCheck = binding.petGenderRG.checkedRadioButtonId
            val checkRadioBtn = view.findViewById<RadioButton>(radioGroupCheck)

            val name = binding.petNameTIET.text.toString().trim()
            val type = binding.petTypeTIET.text.toString().trim()
            val age = binding.petAgeTIET.text.toString().trim()
            val desc = binding.petDescTIET.text.toString().trim()

            // Validate inputs
            var isValid = true

            if (name.isEmpty() || name.length > 100) {
                binding.petNameTIL.error = "Name is required and must be less than 100 characters"
                isValid = false
            } else {
                binding.petNameTIL.error = null
            }

            if (type.isEmpty() || type.length > 100) {
                binding.petTypeTIL.error = "Type is required and must be less than 100 characters"
                isValid = false
            } else {
                binding.petTypeTIL.error = null
            }

            if (age.isEmpty() || age.length > 5) {
                binding.petAgeTIL.error = "Age is required and must be less than 5 characters"
                isValid = false
            } else {
                binding.petAgeTIL.error = null
            }

            if (desc.length > 500) {
                binding.petDescTIL.error = "Description is required and must be less than 500 characters"
                isValid = false
            } else {
                binding.petDescTIL.error = null
            }

            if (radioGroupCheck == -1) {
                Toast.makeText(requireContext(), "Gender must be selected", Toast.LENGTH_SHORT).show()
                isValid = false
            }

            if (isValid) {
                val gender = checkRadioBtn.text.toString()

                // Show success message and reset fields if all fields are filled
                Toast.makeText(requireContext(), "Success Add My Pet", Toast.LENGTH_SHORT).show()
                checkRadioBtn.isChecked = false

                // Upload data to ViewModel
                viewModel.uploadData(name, type, gender, age.toInt(), desc, imageUri)

                // Clear input fields
                binding.petNameTIET.text?.clear()
                binding.petTypeTIET.text?.clear()
                binding.petAgeTIET.text?.clear()
                binding.petDescTIET.text?.clear()
            } else {
                // Show error message if any field is empty
                Toast.makeText(requireContext(), "Failed to Add My Pet", Toast.LENGTH_SHORT).show()
            }
        }

        // Observe loading state and update button text accordingly
        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.addBtn.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.addBtn.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                // Navigate back to previous fragment after adding pet
                findNavController().popBackStack()
            }
        }

        // Handle click on navigation icon (back button)
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}
