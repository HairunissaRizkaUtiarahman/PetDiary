package org.projectPA.petdiary.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.projectPA.petdiary.model.Article

class ArticleViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _articles = MutableLiveData<List<Article>>()
    val articles: LiveData<List<Article>> get() = _articles

    private val _articleDetails = MutableLiveData<Article?>()
    val articleDetails: LiveData<Article?> get() = _articleDetails

    private val _relatedArticles = MutableLiveData<List<Article>>()
    val relatedArticles: LiveData<List<Article>> get() = _relatedArticles

    init {
        fetchArticles()
    }

    fun fetchArticles() {
        viewModelScope.launch {
            val articles = try {
                db.collection("articles")
                    .get().await().let { querySnapshot ->
                        querySnapshot.documents.mapNotNull { it.toObject(Article::class.java) }
                    }
            } catch (e: Exception) {
                emptyList()
            }
            _articles.postValue(articles)
        }
    }

    fun fetchArticleById(articleId: String) {
        viewModelScope.launch {
            val article = try {
                db.collection("articles")
                    .document(articleId)
                    .get().await().toObject(Article::class.java)
            } catch (e: Exception) {
                Log.e("ArticleViewModel", "Error fetching article", e)
                null
            }
            Log.d("ArticleViewModel", "Fetched article: $article")
            _articleDetails.postValue(article)
        }
    }

    fun fetchRelatedArticles(category: String) {
        viewModelScope.launch {
            val relatedArticles = try {
                db.collection("articles")
                    .whereEqualTo("category", category)
                    .get().await().let { querySnapshot ->
                        querySnapshot.documents.mapNotNull { it.toObject(Article::class.java) }
                    }
            } catch (e: Exception) {
                emptyList()
            }
            _relatedArticles.postValue(relatedArticles)
        }
    }

    fun searchArticles(query: String) {
        viewModelScope.launch {
            val articles = try {
                db.collection("articles")
                    .whereEqualTo("tittle", query)
                    .get().await().let { querySnapshot ->
                        querySnapshot.documents.mapNotNull { it.toObject(Article::class.java) }
                    }
            } catch (e: Exception) {
                emptyList()
            }
            _articles.postValue(articles)
        }
    }

    fun filterArticlesByCategory(category: String) {
        viewModelScope.launch {
            val articles = try {
                db.collection("articles")
                    .whereEqualTo("category", category)
                    .get().await().let { querySnapshot ->
                        querySnapshot.documents.mapNotNull { it.toObject(Article::class.java) }
                    }
            } catch (e: Exception) {
                emptyList()
            }
            _articles.postValue(articles)
        }
    }
}
