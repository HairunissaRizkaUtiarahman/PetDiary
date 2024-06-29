package org.projectPA.petdiary.view.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import jp.wasabeef.richeditor.RichEditor
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ActivityWriteArticleForAdminBinding
import org.projectPA.petdiary.model.Article
import org.projectPA.petdiary.viewmodel.ArticleViewModel
import java.util.*

class ActivityWriteArticleForAdmin : AppCompatActivity() {

    private lateinit var binding: ActivityWriteArticleForAdminBinding
    private val viewModel: ArticleViewModel by viewModels()
    private var selectedImageUri: Uri? = null
    private lateinit var editor: RichEditor

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.uploadImageArticle.setImageURI(it)
        }
    }

    private var isHeadingActive = false
    private var isSubheadingActive = false
    private var isBodyActive = false
    private var isBoldActive = false
    private var isItalicActive = false
    private var isUnderlineActive = false
    private var isNumberingActive = false
    private var isBulletPointsActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteArticleForAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRichEditor()
        setupListeners()
        setupCategoryDropdown()
    }

    private fun setupRichEditor() {
        editor = binding.editor
        editor.setEditorHeight(200)
        editor.setEditorFontSize(16)
        editor.setPlaceholder("Write your article...")

        binding.boldTextButton.setOnClickListener { editor.setBold() }
        binding.italicTextButton.setOnClickListener { editor.setItalic() }
        binding.underlineTextButton.setOnClickListener { editor.setUnderline() }
        binding.makeTextToLinkButton.setOnClickListener {
            editor.insertLink("https://www.example.com", "Example Link")
        }
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        binding.publishButton.setOnClickListener {
            publishArticle()
        }

        binding.uploadPhotoButton.setOnClickListener {
            selectImage()
        }

        binding.previewButton.setOnClickListener {
            previewArticle()
        }

        binding.headingTextButton.setOnClickListener {
            isHeadingActive = !isHeadingActive
            isSubheadingActive = false
            isBodyActive = false
            toggleButtonState(binding.headingTextButton, isHeadingActive)
            toggleButtonState(binding.subheadingTextButton, isSubheadingActive)
            editor.setHeading(1)
        }

        binding.subheadingTextButton.setOnClickListener {
            isSubheadingActive = !isSubheadingActive
            isHeadingActive = false
            isBodyActive = false
            toggleButtonState(binding.subheadingTextButton, isSubheadingActive)
            toggleButtonState(binding.headingTextButton, isHeadingActive)
            editor.setHeading(2)
            editor.removeFormat()
        }


        binding.boldTextButton.setOnClickListener {
            isBoldActive = !isBoldActive
            toggleButtonState(binding.boldTextButton, isBoldActive)
            editor.setBold()
        }

        binding.italicTextButton.setOnClickListener {
            isItalicActive = !isItalicActive
            toggleButtonState(binding.italicTextButton, isItalicActive)
            editor.setItalic()
        }

        binding.underlineTextButton.setOnClickListener {
            isUnderlineActive = !isUnderlineActive
            toggleButtonState(binding.underlineTextButton, isUnderlineActive)
            editor.setUnderline()
        }

        binding.listBulletPointsButton.setOnClickListener {
            isBulletPointsActive = !isBulletPointsActive
            isNumberingActive = false
            toggleButtonState(binding.listBulletPointsButton, isBulletPointsActive)
            toggleButtonState(binding.listNumberPoints, isNumberingActive)
            editor.setBullets()
        }

        binding.listNumberPoints.setOnClickListener {
            isNumberingActive = !isNumberingActive
            isBulletPointsActive = false
            toggleButtonState(binding.listNumberPoints, isNumberingActive)
            toggleButtonState(binding.listBulletPointsButton, isBulletPointsActive)
            editor.setNumbers()
        }

        binding.decreaseIndent2.setOnClickListener {
            editor.setOutdent()
        }

        binding.increaseIndent3.setOnClickListener {
            editor.setIndent()
        }

        binding.makeTextToLinkButton.setOnClickListener {
            editor.insertLink("https://www.google.com", "Google")

        }
    }

    private fun previewArticle() {
        val title = binding.inputTittleField.text.toString()
        val body = editor.html
        val category = binding.categoryDropdown.selectedItem.toString()

        val article = Article(
            id = UUID.randomUUID().toString(),
            tittle = title,
            category = category,
            date = Date(),
            body = body,
            imageUrl = selectedImageUri.toString(),
            sourceUrl = ""
        )

        val intent = Intent(this, ActivityPreviewResultArticle::class.java)
        intent.putExtra("article", article)
        startActivity(intent)
    }

    private fun setupCategoryDropdown() {
        val categories = arrayOf("Select article category", "Care and Health", "Training", "Event")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categoryDropdown.adapter = adapter

        binding.publishButton.isEnabled = false

        binding.categoryDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                binding.publishButton.isEnabled = position != 0
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                binding.publishButton.isEnabled = false
            }
        }
    }

    private fun selectImage() {
        pickImageLauncher.launch("image/*")
    }

    private fun publishArticle() {
        val title = binding.inputTittleField.text.toString()
        val body = editor.html
        val category = binding.categoryDropdown.selectedItem.toString()

        val article = Article(
            id = UUID.randomUUID().toString(),
            tittle = title,
            category = category,
            date = Date(),
            body = body,
            imageUrl = selectedImageUri.toString(),
            sourceUrl = ""
        )

        viewModel.saveArticleToFirestore(article, onSuccess = {
            Toast.makeText(this, "Article published successfully", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, HomepageArticleActivity::class.java))
        }, onFailure = { e ->
            Toast.makeText(this, "Failed to publish article: ${e.message}", Toast.LENGTH_SHORT).show()
        })
    }

    private fun toggleButtonState(button: TextView, isActive: Boolean) {
        button.setBackgroundResource(if (isActive) R.drawable.background_selected_format else R.drawable.background_unselected_format_text)
    }

    private fun toggleButtonState(button: ImageView, isActive: Boolean) {
        button.setBackgroundResource(if (isActive) R.drawable.background_selected_format else R.drawable.background_unselected_format_text)
    }
}
