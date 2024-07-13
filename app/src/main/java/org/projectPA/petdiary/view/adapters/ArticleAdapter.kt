package org.projectPA.petdiary.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.projectPA.petdiary.databinding.ListArticleBinding
import org.projectPA.petdiary.model.Article
import java.text.SimpleDateFormat
import java.util.Locale

class ArticleAdapter(
    private val context: Context,
    private var articles: List<Article>,
    private val onArticleClick: (String) -> Unit
) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    class ArticleViewHolder(private val binding: ListArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(article: Article, context: Context, onArticleClick: (String) -> Unit) {
            with(binding) {
                val dateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                Glide.with(context).load(article.imageUrl).into(imageArticle)
                tittleArticle.text = article.title
                categoryArticle.text = article.category
                articleDate.text =
                    dateFormatter.format(article.timeAdded)

                root.setOnClickListener {
                    onArticleClick(article.articleId)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ListArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(articles[position], context, onArticleClick)
    }

    override fun getItemCount() = articles.size

    fun updateData(newArticles: List<Article>) {
        articles = newArticles.sortedByDescending { it.timeAdded }
        notifyDataSetChanged()
    }
}
