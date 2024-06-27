package org.projectPA.petdiary.view.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ActivityArticleHomepageBinding
import org.projectPA.petdiary.view.adapters.ArticleAdapter
import org.projectPA.petdiary.viewmodel.ArticleViewModel

class HomepageArticleActivity : AppCompatActivity() {

    private val viewModel: ArticleViewModel by viewModels()
    private lateinit var binding: ActivityArticleHomepageBinding
    private lateinit var articleAdapter: ArticleAdapter
    private var isModerator: Boolean = false // Flag to check moderator status

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleHomepageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if user is moderator
        isModerator = checkModeratorStatus()

        setupRecyclerView()
        setupListeners()
        observeArticles()

        viewModel.fetchArticles()
    }

    private fun checkModeratorStatus(): Boolean {
        // Replace with actual logic to check moderator status from user data or preferences
        // For example, if using Firebase, you might check the current user's data
        // This is a placeholder for actual implementation
        // Assuming isModerator flag is set elsewhere based on user authentication and data retrieval
        return true // Placeholder logic, replace with actual check
    }

    private fun setupRecyclerView() {
        articleAdapter = ArticleAdapter(this, emptyList()) { articleId ->
            val intent = Intent(this, ArticleActivity::class.java).apply {
                putExtra("ARTICLE_ID", articleId)
            }
            startActivity(intent)
        }
        binding.listArticle.apply {
            layoutManager = LinearLayoutManager(this@HomepageArticleActivity)
            adapter = articleAdapter
        }
    }

    private fun setupListeners() {
        binding.searchButton.setOnClickListener {
            startActivity(Intent(this, SearchArticleActivity::class.java))
        }

        binding.allArticleButton.setOnClickListener {
            viewModel.fetchArticles()
            highlightSelectedCategory(binding.allArticleButton)
        }

        binding.careAndHealthButton.setOnClickListener {
            viewModel.filterArticlesByCategory(getString(R.string.care_and_health))
            highlightSelectedCategory(binding.careAndHealthButton)
        }

        binding.trainingButton.setOnClickListener {
            viewModel.filterArticlesByCategory(getString(R.string.training))
            highlightSelectedCategory(binding.trainingButton)
        }

        binding.communityEventsButton.setOnClickListener {
            viewModel.filterArticlesByCategory(getString(R.string.community_events))
            highlightSelectedCategory(binding.communityEventsButton)
        }

        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        // Show add article button only if user is moderator
        if (isModerator) {
            binding.addArticleOnlyForAdminButton.visibility = View.VISIBLE
            binding.addArticleOnlyForAdminButton.setOnClickListener {
                // Handle click event for adding articles (admin only action)
            }
        } else {
            binding.addArticleOnlyForAdminButton.visibility = View.GONE
        }
    }

    private fun highlightSelectedCategory(selectedCategory: TextView) {
        binding.allArticleButton.setBackgroundResource(if (selectedCategory == binding.allArticleButton) R.drawable.selected_category_background else R.drawable.unselected_category_background)
        binding.careAndHealthButton.setBackgroundResource(if (selectedCategory == binding.careAndHealthButton) R.drawable.selected_category_background else R.drawable.unselected_category_background)
        binding.trainingButton.setBackgroundResource(if (selectedCategory == binding.trainingButton) R.drawable.selected_category_background else R.drawable.unselected_category_background)
        binding.communityEventsButton.setBackgroundResource(if (selectedCategory == binding.communityEventsButton) R.drawable.selected_category_background else R.drawable.unselected_category_background)
    }

    private fun observeArticles() {
        viewModel.articles.observe(this) { articles ->
            articles?.let {
                articleAdapter.updateData(it)
            }
        }
    }
}
