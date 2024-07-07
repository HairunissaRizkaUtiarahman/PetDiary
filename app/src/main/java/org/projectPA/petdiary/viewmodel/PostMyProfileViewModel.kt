package org.projectPA.petdiary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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

class PostMyProfileViewModel(private val postRepository: PostRepository) : ViewModel() {
    private val _myPosts = MutableLiveData<List<Post>>()
    private val _myPost = MutableLiveData<Post>()
    private val _isLoading = MutableLiveData<Boolean>()

    val myPosts: LiveData<List<Post>>
        get() = _myPosts

    val myPost: LiveData<Post>
        get() = _myPost
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val postsRepository =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PetDiaryApplication).postRepository
                PostMyProfileViewModel(postsRepository)
            }
        }
    }

    fun loadData() = viewModelScope.launch {
        withContext(Dispatchers.Main) {
            postRepository.getMyPosts().collect {
                _myPosts.value = it
            }
        }
    }

    fun setPost(post: Post) {
        _myPost.value = post
    }

    fun getPost(postId: String) = viewModelScope.launch(Dispatchers.IO) {
        postRepository.getPost(postId)?.let {
            _myPost.postValue(it)
        }
    }

    fun setLike(postId: String) = viewModelScope.launch(Dispatchers.IO) {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        postRepository.setLike(userId, postId)
    }

    fun updateCommentCount(count: Int) {
        _myPost.value = _myPost.value?.copy(commentCount = count)
    }

    fun deleteData(postId: String) = viewModelScope.launch(Dispatchers.IO) {
        _isLoading.postValue(true)
        postRepository.deletePost(postId)
        _isLoading.postValue(false)
    }
}