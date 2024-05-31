package org.projectPA.petdiary.view.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import org.projectPA.petdiary.databinding.SearchArticleBinding
import org.projectPA.petdiary.view.adapters.ArticleAdapter
import org.projectPA.petdiary.viewmodel.ArticleViewModel

class SearchArticleActivity : AppCompatActivity() {

    private val viewModel: ArticleViewModel by viewModels()
    private lateinit var binding: SearchArticleBinding
    private lateinit var articleAdapter: ArticleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SearchArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
        observeArticles()
    }

    private fun setupRecyclerView() {
        articleAdapter = ArticleAdapter(this, emptyList()) { articleId ->
            val intent = Intent(this, ArticleActivity::class.java).apply {
                putExtra("ARTICLE_ID", articleId)
            }
            startActivity(intent)
        }
        binding.listResultArticle.apply {
            layoutManager = LinearLayoutManager(this@SearchArticleActivity)
            adapter = articleAdapter
        }
    }

    private fun setupListeners() {
        binding.searchArticle.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel.searchArticles(it)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        binding.backToArticleHomePage.setOnClickListener {
            finish()
        }
    }

    private fun observeArticles() {
        viewModel.articles.observe(this) { articles ->
            articles?.let {
                articleAdapter.updateData(it)
            }
        }
    }
}
