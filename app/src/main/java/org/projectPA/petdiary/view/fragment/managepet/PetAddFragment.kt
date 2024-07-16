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
    private val petViewModel: PetViewModel by navGraphViewModels(R.id.pet_nav)
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

        // Memilih gambar dari galeri
        val petImage = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) {
            imageUri = it
            binding.petImageIV.setImageURI(it) // Menampilkan gambar yang dipilih
        }

        // Mengambil gambar menggunakan kamera
        val takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    binding.petImageIV.setImageURI(imageUri) // Menampilkan gambar yang diambil
                }
            }

        // Fungsi untuk mengambil gambar menggunakan kamera
        fun takePicture() {

            // Memeriksa izin kamera
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

                // Membuat URI untuk gambar yang akan diambil
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
                imageUri?.let { takePictureLauncher.launch(it) } // Membuka Kamera
            }
        }

        // Tombol "Pilih Gambar"
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

        // Tombol "Tambah"
        binding.addBtn.setOnClickListener {
            val radioGroupCheck = binding.petGenderRG.checkedRadioButtonId
            val checkRadioBtn = view.findViewById<RadioButton>(radioGroupCheck)

            val name = binding.petNameTIET.text.toString().trim()
            val type = binding.petTypeTIET.text.toString().trim()
            val age = binding.petAgeTIET.text.toString().trim()
            val desc = binding.petDescTIET.text.toString().trim()

            // Validasi input name tidak boleh kosong & max 100 karakter
            if (name.isEmpty() || name.length > 100) {
                Toast.makeText(
                    requireContext(),
                    "Name is required and must be less than 100 characters",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Validasi input type tidak boleh kosong & max 100 karakter
            if (type.isEmpty() || type.length > 100) {
                Toast.makeText(
                    requireContext(),
                    "Type is required and must be less than 100 characters",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Validasi input gender tidak boleh kosong
            if (radioGroupCheck == -1) {
                Toast.makeText(requireContext(), "Gender must be selected", Toast.LENGTH_SHORT)
                    .show()
            }

            // Validasi input age tidak boleh kosong & max 5 karakter
            if (age.isEmpty() || age.length > 5) {
                Toast.makeText(
                    requireContext(),
                    "Age is required and must be less than 5 characters",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Validasi input desc tidak boleh kosong & max 500 karakter
            if (desc.length > 500) {
                Toast.makeText(
                    requireContext(),
                    "Description must be less than 500 characters",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Mengambil nilai gender dari radio button yang dipilih
            val gender = checkRadioBtn.text.toString()
            checkRadioBtn.isChecked = false

            // Mengunggah data hewan peliharaan ke ViewModel
            petViewModel.uploadPet(name, type, gender, age.toInt(), desc, imageUri)
            Toast.makeText(requireContext(), "Success Add My Pet", Toast.LENGTH_SHORT).show()

            // Membersihkan input setelah penambahan hewan peliharaan
            binding.petNameTIET.text?.clear()
            binding.petTypeTIET.text?.clear()
            binding.petAgeTIET.text?.clear()
            binding.petDescTIET.text?.clear()
        }

        // Mengamati perubahan status loading di ViewModel
        petViewModel.isLoading.observe(viewLifecycleOwner) {
            // Mengatur tampilan Loading
            if (it) {
                binding.addBtn.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.addBtn.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                startActivity(Intent(requireActivity(), PetActivity::class.java)) // Memuat Ulang PetActivity
                requireActivity().finish()
            }
        }

        // Tombol Back di TopAppBar untuk kembali ke stack sebelumnya
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}
