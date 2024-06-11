package org.projectPA.petdiary.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.testproject.dataclass.CommentPost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.projectPA.petdiary.PetDiaryApplication

import org.projectPA.petdiary.repository.PostRepository

class CommentPostMyProfileViewModel(private val postRepository: PostRepository) : ViewModel() {
    private val _CommentsPost = MutableLiveData<List<CommentPost>>()

    val commentsPost: LiveData<List<CommentPost>>
        get() = _CommentsPost

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val postsRepository =
                    (this[APPLICATION_KEY] as PetDiaryApplication).postRepository
                CommentPostMyProfileViewModel(postsRepository)
            }
        }
    }

    fun loadData(postId: String) = viewModelScope.launch {
        withContext(Dispatchers.Main) {
            Log.d("CommentPostMyProfile", postId.toString())
            postRepository.getCommentPost(postId).collect {
                _CommentsPost.value = it
            }
        }
    }

    fun uploadData(comment: String, postId: String) = viewModelScope.launch {
        withContext(Dispatchers.Main) {
            postRepository.addCommentPost(comment, postId)
        }
    }
}