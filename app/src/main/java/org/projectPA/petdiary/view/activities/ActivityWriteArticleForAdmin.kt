package org.projectPA.petdiary.view.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.*
import android.text.style.*
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ActivityWriteArticleForAdminBinding
import org.projectPA.petdiary.model.Article
import org.projectPA.petdiary.viewmodel.ArticleViewModel
import java.util.*

class ActivityWriteArticleForAdmin : AppCompatActivity() {

    private lateinit var binding: ActivityWriteArticleForAdminBinding
    private val viewModel: ArticleViewModel by viewModels()
    private var selectedImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.uploadImageArticle.setImageURI(it)
        }
    }

    private var activeTextType: String = "body"
    private lateinit var textWatcher: TextWatcher
    private var isHeadingActive = false
    private var isSubheadingActive = false
    private var isBodyActive = false
    private var isBoldActive = false
    private var isItalicActive = false
    private var isUnderlineActive = false
    private var isNumberingActive = false
    private var isBulletPointsActive = false
    private var isDecreaseIndentActive = false
    private var isIncreaseIndentActive = false
    private var isMakeLinkTextActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteArticleForAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        setupCategoryDropdown()
        setupTextWatcher()
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

        binding.headingTextButton.setOnClickListener {
            activeTextType = "heading"
            isHeadingActive = !isHeadingActive
            isSubheadingActive = false
            isBodyActive = false
            toggleButtonState(binding.headingTextButton, isHeadingActive)
            toggleButtonState(binding.subheadingTextButton, isSubheadingActive)
            toggleButtonState(binding.bodyTextButton, isBodyActive)
        }

        binding.subheadingTextButton.setOnClickListener {
            activeTextType = "subheading"
            isSubheadingActive = !isSubheadingActive
            isHeadingActive = false
            isBodyActive = false
            toggleButtonState(binding.subheadingTextButton, isSubheadingActive)
            toggleButtonState(binding.headingTextButton, isHeadingActive)
            toggleButtonState(binding.bodyTextButton, isBodyActive)
        }

        binding.bodyTextButton.setOnClickListener {
            activeTextType = "body"
            isBodyActive = !isBodyActive
            isHeadingActive = false
            isSubheadingActive = false
            toggleButtonState(binding.bodyTextButton, isBodyActive)
            toggleButtonState(binding.headingTextButton, isHeadingActive)
            toggleButtonState(binding.subheadingTextButton, isSubheadingActive)
        }

        binding.boldTextButton.setOnClickListener {
            isBoldActive = !isBoldActive
            toggleButtonState(binding.boldTextButton, isBoldActive)
        }

        binding.italicTextButton.setOnClickListener {
            isItalicActive = !isItalicActive
            toggleButtonState(binding.italicTextButton, isItalicActive)
        }

        binding.underlineTextButton.setOnClickListener {
            isUnderlineActive = !isUnderlineActive
            toggleButtonState(binding.underlineTextButton, isUnderlineActive)
        }

        binding.listBulletPointsButton.setOnClickListener {
            isBulletPointsActive = !isBulletPointsActive
            isNumberingActive = false
            toggleButtonState(binding.listBulletPointsButton, isBulletPointsActive)
            toggleButtonState(binding.listNumberPoints, isNumberingActive)
            if (isBulletPointsActive) {
                removeNumberedPoints(binding.inputBodyField)
                addBulletPointToCurrentLine(binding.inputBodyField)
            } else {
                removeBulletPoints(binding.inputBodyField)
            }
        }

        binding.listNumberPoints.setOnClickListener {
            isNumberingActive = !isNumberingActive
            isBulletPointsActive = false
            toggleButtonState(binding.listNumberPoints, isNumberingActive)
            toggleButtonState(binding.listBulletPointsButton, isBulletPointsActive)
            if (isNumberingActive) {
                removeBulletPoints(binding.inputBodyField)
                insertNumberedPoints(binding.inputBodyField)
            } else {
                removeNumberedPoints(binding.inputBodyField)
            }
        }

        binding.decreaseIndent2.setOnClickListener {
            isDecreaseIndentActive = !isDecreaseIndentActive
            decreaseIndent(binding.inputBodyField)
            toggleButtonState(binding.decreaseIndent2, isDecreaseIndentActive)
        }

        binding.increaseIndent3.setOnClickListener {
            isIncreaseIndentActive = !isIncreaseIndentActive
            increaseIndent(binding.inputBodyField)
            toggleButtonState(binding.increaseIndent3, isIncreaseIndentActive)
        }

        binding.makeTextToLinkButton.setOnClickListener {
            isMakeLinkTextActive = !isMakeLinkTextActive
            makeTextToLink(binding.inputBodyField)
            toggleButtonState(binding.makeTextToLinkButton, isMakeLinkTextActive)
        }
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
        val body = binding.inputBodyField.text.toString()
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

    private fun setupTextWatcher() {
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val start = binding.inputBodyField.selectionStart
                val end = binding.inputBodyField.selectionEnd

                if (start in 1..end) {
                    applyActiveStyles(s, start, end)
                }
            }
        }

        binding.inputBodyField.addTextChangedListener(textWatcher)

        binding.inputBodyField.setOnEditorActionListener { v, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                if (isNumberingActive) {
                    continueNumbering(binding.inputBodyField)
                    return@setOnEditorActionListener true
                } else if (isBulletPointsActive) {
                    addBulletPointToNewLine(binding.inputBodyField)
                    return@setOnEditorActionListener true
                }
            }
            false
        }
    }

    private fun applyActiveStyles(s: Editable?, start: Int, end: Int) {
        val textSize: Float
        val textStyle: Int

        when (activeTextType) {
            "heading" -> {
                textSize = 25f
                textStyle = android.graphics.Typeface.BOLD
            }
            "subheading" -> {
                textSize = 20f
                textStyle = android.graphics.Typeface.BOLD
            }
            else -> {
                textSize = 16f
                textStyle = android.graphics.Typeface.NORMAL
            }
        }

        if (start > 0) {
            s?.setSpan(AbsoluteSizeSpan(textSize.toInt(), true), start - 1, start, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            s?.setSpan(StyleSpan(textStyle), start - 1, start, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        if (isBoldActive) {
            s?.setSpan(StyleSpan(android.graphics.Typeface.BOLD), start - 1, start, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        if (isItalicActive) {
            s?.setSpan(StyleSpan(android.graphics.Typeface.ITALIC), start - 1, start, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        if (isUnderlineActive) {
            s?.setSpan(UnderlineSpan(), start - 1, start, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    private fun insertBulletPoints(editText: EditText) {
        val start = editText.selectionStart
        val lineStart = findLineStart(editText.text, start)
        editText.text.insert(lineStart, "• ")
    }

    private fun addBulletPointToCurrentLine(editText: EditText) {
        val start = editText.selectionStart
        val lineStart = findLineStart(editText.text, start)
        val lineEnd = findLineEnd(editText.text, start)
        if (!editText.text.substring(lineStart, lineEnd).startsWith("• ")) {
            editText.text.insert(lineStart, "• ")
        } else if (editText.text.substring(lineStart, lineEnd).matches(Regex("^\\d+\\.\\s.*"))) {
            editText.text.replace(lineStart, lineEnd, "• " + editText.text.substring(lineStart + editText.text.substring(lineStart, lineEnd).indexOf(' ') + 1, lineEnd))
        }
    }

    private fun addBulletPointToNewLine(editText: EditText) {
        val start = editText.selectionStart
        editText.text.insert(start, "\n• ")
        editText.setSelection(start + 3)
    }

    private fun insertNumberedPoints(editText: EditText) {
        val start = editText.selectionStart
        val lineStart = findLineStart(editText.text, start)
        val lineEnd = findLineEnd(editText.text, start)
        if (editText.text.substring(lineStart, lineEnd).startsWith("• ")) {
            editText.text.replace(lineStart, lineEnd, "1. " + editText.text.substring(lineStart + 2, lineEnd))
        } else {
            val lines = editText.text.substring(0, start).split("\n")
            val lastLine = lines.lastOrNull()
            val nextNumber = if (lastLine != null && lastLine.matches(Regex("^\\d+\\.\\s.*"))) {
                val number = lastLine.substringBefore('.').toIntOrNull() ?: 0
                number + 1
            } else {
                1
            }
            editText.text.insert(lineStart, "$nextNumber. ")
        }
    }

    private fun removeBulletPoints(editText: EditText) {
        val start = editText.selectionStart
        val lineStart = findLineStart(editText.text, start)
        val lineEnd = findLineEnd(editText.text, start)
        if (editText.text.substring(lineStart, lineEnd).startsWith("• ")) {
            editText.text.replace(lineStart, lineEnd, editText.text.substring(lineStart + 2, lineEnd))
        }
    }

    private fun removeNumberedPoints(editText: EditText) {
        val start = editText.selectionStart
        val lineStart = findLineStart(editText.text, start)
        val lineEnd = findLineEnd(editText.text, start)
        if (editText.text.substring(lineStart, lineEnd).matches(Regex("^\\d+\\.\\s.*"))) {
            val indexOfSpace = editText.text.substring(lineStart, lineEnd).indexOf(' ')
            editText.text.replace(lineStart, lineEnd, editText.text.substring(lineStart + indexOfSpace + 1, lineEnd))
        }
    }

    private fun continueNumbering(editText: EditText) {
        val start = editText.selectionStart
        val text = editText.text.toString()
        val lines = text.substring(0, start).split("\n")
        val lastLine = lines.lastOrNull()
        if (lastLine != null && lastLine.matches(Regex("^\\d+\\.\\s.*"))) {
            val number = lastLine.substringBefore('.').toIntOrNull() ?: 0
            val nextNumber = number + 1
            editText.text.insert(start, "\n$nextNumber. ")
            editText.setSelection(start + nextNumber.toString().length + 3)
        }
    }

    private fun decreaseIndent(editText: EditText) {
        val text = editText.text.toString()
        editText.setText(text.replaceFirst("\t", ""))
        editText.setSelection(editText.text.length)
    }

    private fun increaseIndent(editText: EditText) {
        val text = editText.text.toString()
        editText.setText("\t$text")
        editText.setSelection(editText.text.length)
    }

    private fun makeTextToLink(editText: EditText) {
        val start = editText.selectionStart
        val end = editText.selectionEnd
        val spannable = SpannableStringBuilder(editText.text)
        val linkText = "<a href='your_link'>" + editText.text.substring(start, end) + "</a>"
        spannable.replace(start, end, Html.fromHtml(linkText))
        editText.text = spannable
        editText.setSelection(start, end)
    }

    private fun toggleButtonState(button: TextView, isActive: Boolean) {
        button.setBackgroundResource(if (isActive) R.drawable.background_selected_format else R.drawable.background_unselected_format_text)
    }

    private fun toggleButtonState(button: ImageView, isActive: Boolean) {
        button.setBackgroundResource(if (isActive) R.drawable.background_selected_format else R.drawable.background_unselected_format_text)
    }

    private fun findLineStart(text: CharSequence?, pos: Int): Int {
        var lineStart = pos
        while (lineStart > 0 && text?.get(lineStart - 1) != '\n') {
            lineStart--
        }
        return lineStart
    }

    private fun findLineEnd(text: CharSequence?, pos: Int): Int {
        var lineEnd = pos
        while (lineEnd < text?.length ?: 0 && text?.get(lineEnd) != '\n') {
            lineEnd++
        }
        return lineEnd
    }
}
