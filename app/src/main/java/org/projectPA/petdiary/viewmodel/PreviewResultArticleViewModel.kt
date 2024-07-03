package org.projectPA.petdiary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.projectPA.petdiary.model.Article

class PreviewResultArticleViewModel : ViewModel() {

    private val _article = MutableLiveData<Article>()
    val article: LiveData<Article> get() = _article

    fun setArticle(article: Article) {
        _article.value = article
    }

}
