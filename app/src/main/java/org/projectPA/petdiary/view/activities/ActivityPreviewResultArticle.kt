package org.projectPA.petdiary.view.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
        binding.tittleArticle.text = article.tittle
        binding.articleCategory.text = article.category
        binding.articleDate.text = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(article.date)
        binding.articleBody.text = article.body

        Glide.with(this)
            .load(article.imageUrl)
            .into(binding.articleImage)
    }

    private fun publishArticle(article: Article) {
        viewModel.saveArticleToFirestore(article, onSuccess = {
            Toast.makeText(this, "Article published successfully", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, HomepageArticleActivity::class.java))
        }, onFailure = { e ->
            Toast.makeText(this, "Failed to publish article: ${e.message}", Toast.LENGTH_SHORT).show()
        })
    }
}
