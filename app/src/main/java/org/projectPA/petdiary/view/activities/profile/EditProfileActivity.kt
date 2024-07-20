package org.projectPA.petdiary.view.activities.profile

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ActivityEditProfileBinding
import org.projectPA.petdiary.viewmodel.MyProfileViewModel

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private val viewModel: MyProfileViewModel by viewModels { MyProfileViewModel.Factory }
    private val CAMERA_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var imageUri: Uri? = null

        // Memilih gambar dari galeri
        val profileImage = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) {
            imageUri = it
            binding.profileIV.setImageURI(it) // Menampilkan gambar yang dipilih
        }

        // Mengambil gambar menggunakan kamera
        val takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    binding.profileIV.setImageURI(imageUri) // Menampilkan gambar yang diambil
                }
            }

        // Fungsi untuk mengambil gambar menggunakan kamera
        fun takePicture() {

            // Memeriksa izin kamera
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
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
                imageUri = this.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )
                imageUri?.let { takePictureLauncher.launch(it) } // Membuka Kamera
            }
        }

        // Tombol "Pilih Gambar"
        binding.profileIV.setOnClickListener {
            val options = arrayOf("Take Picture", "Choose from Gallery")
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Select Image")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> takePicture()
                        1 -> profileImage.launch("image/*")
                    }
                }
            builder.show()
        }

        // Tombol "Simpan"
        binding.saveBtn.setOnClickListener {
            // Mendapatkan radio button yang dipilih
            val radioGroupCheck = binding.genderRG.checkedRadioButtonId
            val checkRadioBtn = findViewById<RadioButton>(radioGroupCheck)

            // Mengambil input nama, alamat, dan bio
            val name = binding.nameTIET.text.toString().trim()
            val address = binding.addressTIET.text.toString().trim()
            val bio = binding.bioTIET.text.toString().trim()

            // Validasi nama
            if (name.isEmpty() || name.length > 100) {
                Toast.makeText(
                    this,
                    "Name is required and must be less than 100 characters",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Validasi alamat
            if (address.isEmpty() || address.length > 150) {
                Toast.makeText(
                    this,
                    "Address is required and must be less than 150 characters",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Validasi gender
            if (radioGroupCheck == -1) {
                Toast.makeText(this, "Gender must be selected", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // Validasi bio
            if (bio.length > 100) {
                Toast.makeText(
                    this,
                    "Bio must be less than 100 characters",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Mendapatkan teks dari radio button yang dipilih (gender)
            val gender = checkRadioBtn.text.toString()

            // Memeriksa apakah nama sudah ada
            viewModel.checkIfNameExists(name) { nameExists ->
                if (nameExists) {
                    Toast.makeText(this, "Name already taken", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    viewModel.updateMyProfile(name, address, gender, bio, imageUri)
                    Toast.makeText(
                        this,
                        "Success Update My Profile",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        // Mengamati perubahan status loading di ViewModel
        viewModel.isLoading.observe(this) {
            if (it) {
                binding.saveBtn.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.saveBtn.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                finish() // Kembali ke aktivitas sebelumnya saat selesai
            }
        }

        // Memuat data profil pengguna
        viewModel.loadMyProfile()

        // Mengisi tampilan dengan data hewan peliharaan
        viewModel.myProfile.observe(this) { user ->
            user?.let {
                binding.nameTIET.setText(it.name)
                binding.emailTIET.setText(it.email)
                binding.addressTIET.setText(it.address)

                if (it.gender == "Male" || it.gender == "Pria") {
                    binding.maleRB.isChecked = true
                } else if (it.gender == "Female" || it.gender == "Wanita") {
                    binding.femaleRB.isChecked = true
                }

                binding.bioTIET.setText(it.bio)

                Glide.with(binding.profileIV.context)
                    .load(it.imageUrl)
                    .placeholder(R.drawable.image_profile)
                    .into(binding.profileIV)
            }
        }

        // Tombol Back di TopAppBar untuk mengakahiri activity
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }
    }
}
