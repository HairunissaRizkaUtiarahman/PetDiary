package org.projectPA.petdiary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
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

    private val _isModerator = MutableLiveData<Boolean>()
    val isModerator: LiveData<Boolean> get() = _isModerator

    init {
        fetchArticles()
        checkModeratorStatus()
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
                null
            }
            _articleDetails.postValue(article)
        }
    }

    fun fetchRelatedArticles(category: String, currentArticleId: String) {
        viewModelScope.launch {
            val relatedArticles = try {
                db.collection("articles")
                    .whereEqualTo("category", category)
                    .get().await().let { querySnapshot ->
                        querySnapshot.documents.mapNotNull { it.toObject(Article::class.java) }
                            .filter { it.id != currentArticleId }
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
                    .get().await().let { querySnapshot ->
                        querySnapshot.documents.mapNotNull { it.toObject(Article::class.java) }
                            .filter { it.tittle.contains(query, ignoreCase = true) }
                    }
            } catch (e: Exception) {
                emptyList()
            }
            _articles.postValue(articles)
        }
    }

    fun fetchRandomArticles() {
        viewModelScope.launch {
            val articles = try {
                db.collection("articles")
                    .get().await().let { querySnapshot ->
                        querySnapshot.documents.mapNotNull { it.toObject(Article::class.java) }
                    }.shuffled().take(10)
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

    fun saveArticleToFirestore(article: Article, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                db.collection("articles").document(article.id).set(article).await()
                onSuccess()
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    private fun checkModeratorStatus() {
        viewModelScope.launch {
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.let { user ->
                val isModerator = try {
                    val document = db.collection("users").document(user.uid).get().await()
                    document.getBoolean("isModerator") ?: false
                } catch (e: Exception) {
                    false
                }
                _isModerator.postValue(isModerator)
            } ?: _isModerator.postValue(false)
        }
    }
}
