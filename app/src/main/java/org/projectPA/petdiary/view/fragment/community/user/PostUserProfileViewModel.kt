package org.projectPA.petdiary.view.fragment.community.user

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

class PostUserProfileViewModel(private val postRepository: PostRepository) : ViewModel() {
    private val _posts = MutableLiveData<List<Post>>()
    private val _post = MutableLiveData<Post>()

    val posts: LiveData<List<Post>>
        get() = _posts

    val post: LiveData<Post>
        get() = _post

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val postsRepository =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PetDiaryApplication).postRepository
                PostUserProfileViewModel(postsRepository)
            }
        }
    }

    fun loadData(userId: String) = viewModelScope.launch {
        withContext(Dispatchers.Main) {
            postRepository.getPostsUserProfile(userId).collect {
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