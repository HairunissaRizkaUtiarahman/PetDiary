package org.projectPA.petdiary.view.activities.community

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.projectPA.petdiary.databinding.ActivityAddPostCommunityBinding
import org.projectPA.petdiary.viewmodel.PostViewModel

class AddPostCommunityActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPostCommunityBinding
    private val viewModel: PostViewModel by viewModels { PostViewModel.Factory }
    private val CAMERA_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostCommunityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var imageUri: Uri? = null

        // Memilih gambar dari galeri
        val postImage = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) {
            imageUri = it
            binding.postImageIV.setImageURI(it) // Menampilkan gambar yang dipilih
        }

        // Mengambil gambar menggunakan kamera
        val takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    binding.postImageIV.setImageURI(imageUri) // Menampilkan gambar yang diambil
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
                imageUri = contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )
                imageUri?.let { takePictureLauncher.launch(it) } // Membuka Kamera
            }
        }

        // Tombol "Pilih Gambar"
        binding.postImageIV.setOnClickListener {
            val options = arrayOf("Take Picture", "Choose from Gallery")
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Select Image")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> takePicture()
                        1 -> postImage.launch("image/*")
                    }
                }
            builder.show()
        }

        // Tombol "Post"
        binding.postBtn.setOnClickListener {
            val caption = binding.descTIET.text.toString().trim()

            // Validasi input desc tidak boleh kosong & max 100 karakter
            if (caption.isEmpty() || caption.length > 1000) {
                Toast.makeText(
                    this,
                    "Description is required and must be less than 1000 characters",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Mengunggah data Post ke ViewModel
            viewModel.uploadData(caption, imageUri)
            Toast.makeText(this, "Success Upload Post", Toast.LENGTH_SHORT).show()

            binding.descTIET.text?.clear()
        }

        // Mengamati perubahan status loading di ViewModel
        viewModel.isLoading.observe(this) {
            if (it) {
                binding.postBtn.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.postBtn.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE

                startActivity(Intent(this, CommunityHomePageActivity::class.java)) // Navigasi ke CommunityHomePageActivity
                finish()
            }
        }

        // Tombol Back di TopAppBar untuk mengakahiri activity
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }
    }
}