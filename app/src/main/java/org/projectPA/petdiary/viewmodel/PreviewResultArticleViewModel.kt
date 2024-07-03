package org.projectPA.petdiary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.projectPA.petdiary.model.Article

class PreviewResultArticleViewModel : ViewModel() {

    private val _article = MutableLiveData<Article>()
    val article: LiveData<Article> get() = _article

    private val db = FirebaseFirestore.getInstance()

    fun setArticle(article: Article) {
        _article.value = article
    }

    fun saveArticleToFirestore(article: Article, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                db.collection("articles").document(article.articleId).set(article).await()
                onSuccess()
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }
}
