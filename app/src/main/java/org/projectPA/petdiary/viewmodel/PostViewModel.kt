package org.projectPA.petdiary.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.projectPA.petdiary.PetDiaryApplication
import org.projectPA.petdiary.model.Post
import org.projectPA.petdiary.repository.PostRepository

class PostViewModel(private val postRepository: PostRepository) : ViewModel() {
    private val _posts = MutableLiveData<List<Post>>()
    private val _post = MutableLiveData<Post>()
    private val _isLoading = MutableLiveData<Boolean>()

    val posts: LiveData<List<Post>>
        get() = _posts

    val post: LiveData<Post>
        get() = _post

    val isLoading: LiveData<Boolean>
        get() = _isLoading

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val postsRepository =
                    (this[APPLICATION_KEY] as PetDiaryApplication).postRepository
                PostViewModel(postsRepository)
            }
        }
    }

    fun uploadData(desc: String, uri: Uri?) = viewModelScope.launch(Dispatchers.IO) {
        _isLoading.postValue(true)
        postRepository.addPost(desc, uri)
        _isLoading.postValue(false)
    }

    fun loadData() = viewModelScope.launch {
        withContext(Dispatchers.Main) {
            postRepository.getPosts().collect {
                _posts.value = it
            }
        }
    }

    fun setPost(post: Post) {
        _post.value = post
    }

    fun getPost(postId: String) = viewModelScope.launch(Dispatchers.IO) {
        postRepository.getPost(postId)?.let {
            _post.postValue(it)
        }
    }

    fun setLike(postId: String) = viewModelScope.launch(Dispatchers.IO) {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        postRepository.setLike(userId, postId)
    }
}