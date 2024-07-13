package org.projectPA.petdiary.view.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import jp.wasabeef.richeditor.RichEditor
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ActivityWriteArticleForAdminBinding
import org.projectPA.petdiary.model.Article
import org.projectPA.petdiary.viewmodel.ArticleViewModel
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicReference

class ActivityWriteArticleForAdmin : AppCompatActivity() {

    private lateinit var binding: ActivityWriteArticleForAdminBinding
    val viewModel: ArticleViewModel by viewModels()
    private var selectedImageUri: Uri? = null
    private lateinit var editor: RichEditor
    private lateinit var nestedScrollView: NestedScrollView

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

        nestedScrollView = binding.scrollView

        setupRichEditor()
        setupListeners()
        setupCategoryDropdown()
    }

    private fun setupRichEditor() {
        editor = binding.editor
        editor.setEditorHeight(200)
        editor.setEditorFontSize(16)
        editor.setPlaceholder("Write your article...")

        editor.settings.javaScriptEnabled = true

        editor.addJavascriptInterface(JavaScriptInterface(this), "JSInterface")

        binding.boldTextButton.setOnClickListener { editor.setBold() }
        binding.italicTextButton.setOnClickListener { editor.setItalic() }
        binding.underlineTextButton.setOnClickListener { editor.setUnderline() }

        editor.setOnTextChangeListener {
            editor.loadUrl(
                "javascript:(function() {" +
                        "var elem = document.activeElement;" +
                        "var rect = elem.getBoundingClientRect();" +
                        "window.JSInterface.scrollToCursor(rect.top, rect.height);" +
                        "})()"
            )
        }
    }

    fun setRichEditorText(text: String) {
        runOnUiThread {
            editor.loadUrl("javascript:RE.setHtml('$text');")
        }
    }

    fun getRichEditorText(callback: (String) -> Unit) {
        editor.evaluateJavascript("javascript:RE.getHtml();") { value ->
            callback(value)
        }
    }

    // JavaScript interface for scrolling to cursor
    class JavaScriptInterface(private val activity: ActivityWriteArticleForAdmin) {
        @JavascriptInterface
        fun scrollToCursor(top: Float, height: Float) {
            activity.nestedScrollView.post {
                activity.nestedScrollView.smoothScrollTo(0, (top - height).toInt())
            }
        }
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener {
            Log.d("ActivityWriteArticleForAdmin", "Back button clicked")
            onBackPressed()
        }

        binding.publishButton.setOnClickListener {
            Log.d("ActivityWriteArticleForAdmin", "Publish button clicked")
            if (isFormValid()) {
                selectedImageUri?.let {
                    val article = createArticle()
                    viewModel.uploadImageAndSaveArticle(it, article, onSuccess = {
                        Toast.makeText(this, "Article published successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, HomepageArticleActivity::class.java))
                    }, onFailure = { e ->
                        Toast.makeText(this, "Failed to publish article: ${e.message}", Toast.LENGTH_SHORT).show()
                    })
                }
            } else {
                Log.d("ActivityWriteArticleForAdmin", "Form is not valid. Showing popup.")
                showIncompleteFormPopup()
            }
        }

        binding.uploadPhotoButton.setOnClickListener {
            Log.d("ActivityWriteArticleForAdmin", "Upload photo button clicked")
            selectImage()
        }

        binding.previewButton.setOnClickListener {
            Log.d("ActivityWriteArticleForAdmin", "Preview button clicked")
            previewArticle()
        }

        binding.undoButton.setOnClickListener {
            Log.d("ActivityWriteArticleForAdmin", "Undo button clicked")
            editor.undo()
        }

        binding.redoButton.setOnClickListener {
            Log.d("ActivityWriteArticleForAdmin", "Redo button clicked")
            editor.redo()
        }

        binding.headingTextButton.setOnClickListener {
            Log.d("ActivityWriteArticleForAdmin", "Heading button clicked")
            isHeadingActive = !isHeadingActive
            isSubheadingActive = false
            isBodyActive = false
            toggleButtonState(binding.headingTextButton, isHeadingActive)
            toggleButtonState(binding.subheadingTextButton, isSubheadingActive)
            editor.setHeading(2)
        }

        binding.subheadingTextButton.setOnClickListener {
            Log.d("ActivityWriteArticleForAdmin", "Subheading button clicked")
            isSubheadingActive = !isSubheadingActive
            isHeadingActive = false
            isBodyActive = false
            toggleButtonState(binding.subheadingTextButton, isSubheadingActive)
            toggleButtonState(binding.headingTextButton, isHeadingActive)
            editor.setHeading(3)
        }

        binding.boldTextButton.setOnClickListener {
            Log.d("ActivityWriteArticleForAdmin", "Bold button clicked")
            isBoldActive = !isBoldActive
            toggleButtonState(binding.boldTextButton, isBoldActive)
            editor.setBold()
        }

        binding.italicTextButton.setOnClickListener {
            Log.d("ActivityWriteArticleForAdmin", "Italic button clicked")
            isItalicActive = !isItalicActive
            toggleButtonState(binding.italicTextButton, isItalicActive)
            editor.setItalic()
        }

        binding.underlineTextButton.setOnClickListener {
            Log.d("ActivityWriteArticleForAdmin", "Underline button clicked")
            isUnderlineActive = !isUnderlineActive
            toggleButtonState(binding.underlineTextButton, isUnderlineActive)
            editor.setUnderline()
        }

        binding.listBulletPointsButton.setOnClickListener {
            Log.d("ActivityWriteArticleForAdmin", "Bullet points button clicked")
            isBulletPointsActive = !isBulletPointsActive
            isNumberingActive = false
            toggleButtonState(binding.listBulletPointsButton, isBulletPointsActive)
            toggleButtonState(binding.listNumberPoints, isNumberingActive)
            editor.setBullets()
        }

        binding.listNumberPoints.setOnClickListener {
            Log.d("ActivityWriteArticleForAdmin", "Number points button clicked")
            isNumberingActive = !isNumberingActive
            isBulletPointsActive = false
            toggleButtonState(binding.listNumberPoints, isNumberingActive)
            toggleButtonState(binding.listBulletPointsButton, isBulletPointsActive)
            editor.setNumbers()
        }

        binding.decreaseIndent2.setOnClickListener {
            Log.d("ActivityWriteArticleForAdmin", "Decrease indent button clicked")
            editor.setOutdent()
        }

        binding.increaseIndent3.setOnClickListener {
            Log.d("ActivityWriteArticleForAdmin", "Increase indent button clicked")
            editor.setIndent()
        }
    }

    private fun createArticle(): Article {
        val title = binding.inputTittleField.text.toString()
        val body = editor.html
        val category = binding.categoryDropdown.selectedItem.toString()

        return Article(
            articleId = UUID.randomUUID().toString(),
            title = title,
            category = category,
            timeAdded = Date(),
            articleText = body,
            imageUrl = "",
            sourceUrl = ""
        )
    }

    private fun previewArticle() {
        if (!isFormValid()) {
            showIncompleteFormPopup()
            return
        }

        val article = createArticle().apply {
            imageUrl = selectedImageUri.toString()
        }

        val intent = Intent(this, ActivityPreviewResultArticle::class.java)
        intent.putExtra("article", article)
        startActivity(intent)
    }

    private fun setupCategoryDropdown() {
        val usageOptions = resources.getStringArray(R.array.article_category)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, usageOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categoryDropdown.adapter = adapter
    }

    private fun selectImage() {
        pickImageLauncher.launch("image/*")
    }

    fun isFormValid(): Boolean {
        val isTitleValid = binding.inputTittleField.text.toString().isNotEmpty()
        val isCategoryValid = binding.categoryDropdown.selectedItemPosition != 0
        val isBodyValid = editor.html?.isNotEmpty() == true
        val isImageValid = selectedImageUri != null

        Log.d("ActivityWriteArticleForAdmin", "Title valid: $isTitleValid")
        Log.d("ActivityWriteArticleForAdmin", "Category valid: $isCategoryValid")
        Log.d("ActivityWriteArticleForAdmin", "Body valid: $isBodyValid")
        Log.d("ActivityWriteArticleForAdmin", "Image valid: $isImageValid")

        return isTitleValid && isCategoryValid && isBodyValid && isImageValid
    }

    private fun showIncompleteFormPopup() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.popup_message_unclompete_article, null)
        val closeButton = dialogLayout.findViewById<Button>(R.id.close_button)

        builder.setView(dialogLayout)
        val dialog = builder.create()
        closeButton.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun toggleButtonState(button: TextView, isActive: Boolean) {
        button.setBackgroundResource(if (isActive) R.drawable.background_selected_format else R.drawable.background_unselected_format_text)
    }

    private fun toggleButtonState(button: ImageView, isActive: Boolean) {
        button.setBackgroundResource(if (isActive) R.drawable.background_selected_format else R.drawable.background_unselected_format_text)
    }
}
