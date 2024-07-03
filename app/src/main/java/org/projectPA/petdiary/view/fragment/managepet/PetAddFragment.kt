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
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentPetAddBinding
import org.projectPA.petdiary.view.activities.managepet.PetActivity
import org.projectPA.petdiary.viewmodel.PetViewModel

class PetAddFragment : Fragment() {
    private lateinit var binding: FragmentPetAddBinding
    private val viewModel: PetViewModel by navGraphViewModels(R.id.pet_nav)
    private val CAMERA_PERMISSION_REQUEST_CODE = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPetAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var imageUri: Uri? = null

        val petImage = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) {
            imageUri = it
            binding.petImageIV.setImageURI(it)
        }

        val takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    binding.petImageIV.setImageURI(imageUri)
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
                        1 -> petImage.launch("image/*")
                    }
                }
            builder.show()
        }

        binding.addBtn.setOnClickListener {
            val radioGroupCheck = binding.petGenderRG.checkedRadioButtonId
            val checkRadioBtn = view.findViewById<RadioButton>(radioGroupCheck)

            val name = binding.petNameTIET.text.toString().trim()
            val type = binding.petTypeTIET.text.toString().trim()
            val age = binding.petAgeTIET.text.toString().trim()
            val desc = binding.petDescTIET.text.toString().trim()

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

            viewModel.uploadPet(name, type, gender, age.toInt(), desc, imageUri)
            Toast.makeText(requireContext(), "Success Add My Pet", Toast.LENGTH_SHORT).show()

            binding.petNameTIET.text?.clear()
            binding.petTypeTIET.text?.clear()
            binding.petAgeTIET.text?.clear()
            binding.petDescTIET.text?.clear()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.addBtn.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.addBtn.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                startActivity(Intent(requireActivity(), PetActivity::class.java))
                requireActivity().finish()
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}
