package org.projectPA.petdiary.view.activities

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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

        val postImage = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) {
            imageUri = it
            binding.postImageIV.setImageURI(it)
        }

        val takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    binding.postImageIV.setImageURI(imageUri)
                }
            }

        fun takePicture() {
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
                imageUri?.let { takePictureLauncher.launch(it) }
            }
        }

        binding.pickBtn.setOnClickListener {
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

        binding.postBtn.setOnClickListener {
            val desc = binding.descTextInputEditText.text.toString().trim()

            if (desc != "") {
                viewModel.uploadData(desc, imageUri)
                Toast.makeText(this, "Success Upload Post", Toast.LENGTH_SHORT).show()
                binding.descTextInputEditText.text?.clear()
            }
        }

        viewModel.isLoading.observe(this) {
            if (it) {
                binding.postBtn.text = "UPLOADING..."
            } else {
                binding.postBtn.text = "POST"
                startActivity(Intent(this, CommunityHomePageActivity::class.java))
                finish()
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }
    }
}