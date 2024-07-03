package org.projectPA.petdiary.view.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import org.jsoup.Jsoup
import org.projectPA.petdiary.databinding.ActivityArticleBinding
import org.projectPA.petdiary.model.Article
import org.projectPA.petdiary.view.adapters.ArticleAdapter
import org.projectPA.petdiary.viewmodel.ArticleViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class ArticleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArticleBinding
    private val viewModel: ArticleViewModel by viewModels()
    private lateinit var articleId: String
    private lateinit var relatedArticlesAdapter: ArticleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        articleId = intent.getStringExtra("ARTICLE_ID") ?: ""
        Log.d("ArticleActivity", "Received ARTICLE_ID: $articleId")
        viewModel.fetchArticleById(articleId)

        setupRecyclerView()
        observeArticleDetails()
        observeRelatedArticles()

        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        binding.shareButton.setOnClickListener {
            shareArticle()
        }
    }

    private fun setupRecyclerView() {
        relatedArticlesAdapter = ArticleAdapter(this, emptyList()) { articleId ->
            val intent = Intent(this, ArticleActivity::class.java).apply {
                putExtra("ARTICLE_ID", articleId)
            }
            startActivity(intent)
        }
        binding.listRelatedArticle.apply {
            layoutManager = LinearLayoutManager(this@ArticleActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = relatedArticlesAdapter
        }
    }

    private fun observeArticleDetails() {
        viewModel.articleDetails.observe(this) { article ->
            article?.let {
                displayArticleDetails(it)
                viewModel.fetchRelatedArticles(it.category, articleId)
            }
        }
    }

    private fun observeRelatedArticles() {
        viewModel.relatedArticles.observe(this) { articles ->
            articles?.let {
                relatedArticlesAdapter.updateData(it)
            }
        }
    }

    private fun displayArticleDetails(article: Article) {
        binding.tittleArticle.text = article.tittle
        binding.articleCategory.text = article.category
        binding.articleDate.text = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(article.date)

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
        binding.articleBody.loadDataWithBaseURL(null, article.body, "text/html", "UTF-8", null)

        Glide.with(this)
            .load(article.imageUrl)
            .into(binding.articleImage)
    }

    private fun shareArticle() {
        val article = viewModel.articleDetails.value
        article?.let {
            val plainTextBody = Jsoup.parse(it.body).text()
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Check out this article from PetDiary App: ${it.tittle}\n\n$plainTextBody")
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Share article via"))
        }
    }
}
