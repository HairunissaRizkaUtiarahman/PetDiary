package org.projectPA.petdiary.view.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import org.projectPA.petdiary.databinding.ActivityPreviewResultArticleBinding
import org.projectPA.petdiary.model.Article
import org.projectPA.petdiary.viewmodel.PreviewResultArticleViewModel
import java.text.SimpleDateFormat
import java.util.*

class ActivityPreviewResultArticle : AppCompatActivity() {

    private lateinit var binding: ActivityPreviewResultArticleBinding
    private val viewModel: PreviewResultArticleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewResultArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        val article = intent.getSerializableExtra("article") as? Article
        article?.let {
            viewModel.setArticle(it)
        }

        viewModel.article.observe(this) { article ->
            article?.let {
                displayArticleDetails(it)
            }
        }
    }

    private fun displayArticleDetails(article: Article) {
        binding.tittleArticle.text = article.title
        binding.articleCategory.text = article.category
        binding.articleDate.text = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(article.timeAdded)

        binding.articleBody.settings.javaScriptEnabled = true
        binding.articleBody.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: android.webkit.WebResourceRequest): Boolean {
                val url = request.url.toString()
                if (Uri.parse(url).host != null) {
                    return false //
                }
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                return true
            }
        }
        binding.articleBody.loadDataWithBaseURL(null, article.articleText, "text/html", "UTF-8", null)

        Glide.with(this)
            .load(article.imageUrl)
            .into(binding.articleImage)
    }
}
