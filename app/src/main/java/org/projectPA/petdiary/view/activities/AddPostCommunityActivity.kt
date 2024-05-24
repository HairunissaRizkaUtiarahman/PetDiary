package org.projectPA.petdiary.view.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
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

        val inputFilter = InputFilter.LengthFilter(250)
        binding.descTextInputEditText.filters = arrayOf(inputFilter)

        binding.postBtn.setOnClickListener {
            val desc = binding.descTextInputEditText.text.toString().trim()

            if (desc.isEmpty()) {
                Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show()
            } else if (desc.length > 250) {
                Toast.makeText(this, "Comment cannot exceed 500 characters", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.uploadData(desc, uri)
                Toast.makeText(this, "Success send comment", Toast.LENGTH_SHORT).show()
                binding.descTextInputEditText.text?.clear()
            }

        }

        viewModel.isLoading.observe(this) {
            if (it) {
                binding.postBtn.text = "UPLOADING..."
            } else {
                binding.postBtn.text = "POST"
                val intent = Intent(this, CommunityHomePageActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}