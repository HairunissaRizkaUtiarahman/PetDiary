package org.projectPA.petdiary.view.activities

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import org.projectPA.petdiary.databinding.ActivityAddPostCommunityBinding
import org.projectPA.petdiary.viewmodel.PostViewModel

class AddPostCommunityActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPostCommunityBinding
    private val viewModel: PostViewModel by viewModels { PostViewModel.Factory }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddPostCommunityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var uri: Uri? = null

        val postImage = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) {
            binding.postImageIV.setImageURI(it)
            if (it != null) {
                uri = it
            }
        }

        binding.pickBtn.setOnClickListener {
            postImage.launch("image/*")
        }

        binding.postBtn.setOnClickListener {
            val desc = binding.descTextInputEditText.text.toString().trim()

            if (desc != "") {
                viewModel.uploadData(desc, uri)
                Toast.makeText(this, "Success Upload Post", Toast.LENGTH_SHORT).show()
                binding.descTextInputEditText.text?.clear()
            }
        }

        viewModel.isLoading.observe(this) {
            if (it) {
                binding.postBtn.text = "UPLOADING..."
            } else {
                binding.postBtn.text = "POST"
                finish()
            }
        }
    }
}