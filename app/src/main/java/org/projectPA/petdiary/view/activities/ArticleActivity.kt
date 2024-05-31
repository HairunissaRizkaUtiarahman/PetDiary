package org.projectPA.petdiary.view.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
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
                viewModel.fetchRelatedArticles(it.category)
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
        val dateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        with(binding) {
            Glide.with(this@ArticleActivity).load(article.imageUrl).into(articleImage)
            tittleArticle.text = article.tittle
            articleCategory.text = article.category
            articleDate.text = dateFormatter.format(article.date)
            articleBody.text = article.body


            linkSumberArticle.text = article.sourceUrl
            linkSumberArticle.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.sourceUrl))
                startActivity(intent)
            }

            Log.d("ArticleActivity", "Displayed article details: $article")
        }
    }

    private fun shareArticle() {
        val article = viewModel.articleDetails.value
        article?.let {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Check out this article: ${it.tittle}\n\n${it.body}\n\nRead more at: ${it.sourceUrl}")
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Share article via"))
        }
    }
}
