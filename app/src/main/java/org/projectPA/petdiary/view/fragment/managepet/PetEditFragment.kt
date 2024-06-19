package org.projectPA.petdiary.view.fragment.managepet

import android.Manifest
import android.content.ContentValues
import android.content.Intent
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
import com.bumptech.glide.Glide
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentPetEditBinding
import org.projectPA.petdiary.view.activities.managepet.PetActivity
import org.projectPA.petdiary.viewmodel.PetViewModel

class PetEditFragment : Fragment() {
    private lateinit var binding: FragmentPetEditBinding
    private val viewModel: PetViewModel by navGraphViewModels(R.id.pet_nav)
    private val CAMERA_PERMISSION_REQUEST_CODE = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPetEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var imageUri: Uri? = null

        // Register activity result for picking image from gallery
        val petImage = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) {
            imageUri = it
            binding.petImageIV.setImageURI(it)
        }

        // Register activity result for taking picture
        val takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    binding.petImageIV.setImageURI(imageUri)
                }
            }

        // Function to handle taking picture
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

        // Handle click on pick button to select image from gallery or take picture
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

        // Handle click on save button to update pet data
        binding.saveBtn.setOnClickListener {
            val radioGroupCheck = binding.petGenderRG.checkedRadioButtonId
            val checkRadioBtn = view.findViewById<RadioButton>(radioGroupCheck)

            val name = binding.petNameTIET.text.toString().trim()
            val type = binding.petTypeTIET.text.toString().trim()
            val age = binding.petAgeTIET.text.toString().trim()
            val desc = binding.petDescTIET.text.toString().trim()

            // Validate inputs
            if (name.isEmpty() || name.length > 100) {
                Toast.makeText(
                    requireContext(),
                    "Name is required and must be less than 100 characters",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (type.isEmpty() || type.length > 100) {
                Toast.makeText(
                    requireContext(),
                    "Type is required and must be less than 100 characters",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (radioGroupCheck == -1) {
                Toast.makeText(requireContext(), "Gender must be selected", Toast.LENGTH_SHORT)
                    .show()
            }

            if (age.isEmpty() || age.length > 5) {
                Toast.makeText(
                    requireContext(),
                    "Age is required and must be less than 5 characters",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (desc.length > 500) {
                Toast.makeText(
                    requireContext(),
                    "Description must be less than 500 characters",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val gender = checkRadioBtn.text.toString()
            checkRadioBtn.isChecked = false
            val myPetId = viewModel.pet.value?.id ?: ""

            viewModel.updatePet(myPetId, name, type, gender, age.toInt(), desc, imageUri)
            Toast.makeText(requireContext(), "Success Update My Pet", Toast.LENGTH_SHORT).show()

            binding.petNameTIET.text?.clear()
            binding.petTypeTIET.text?.clear()
            binding.petAgeTIET.text?.clear()
            binding.petDescTIET.text?.clear()
        }

        // Observe loading state and update button text accordingly
        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.saveBtn.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.saveBtn.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                // Navigate back to PetActivity
                startActivity(Intent(requireActivity(), PetActivity::class.java))
                requireActivity().finish()
            }
        }

        // Observe pet data and populate UI fields
        viewModel.pet.observe(viewLifecycleOwner) { pet ->
            with(binding) {
                if (pet.gender == "Male" || pet.gender == "Jantan") {
                    maleRB.isChecked = true
                } else if (pet.gender == "Female" || pet.gender == "Betina") {
                    femaleRB.isChecked = true
                }

                petNameTIET.setText(pet.name)
                petTypeTIET.setText(pet.type)
                petAgeTIET.setText(pet.age.toString())
                petDescTIET.setText(pet.desc)

                Glide.with(petImageIV.context).load(pet.imageUrl)
                    .placeholder(R.drawable.image_blank)
                    .into(petImageIV)
            }
        }

        // Handle click on navigation icon (back button)
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}
