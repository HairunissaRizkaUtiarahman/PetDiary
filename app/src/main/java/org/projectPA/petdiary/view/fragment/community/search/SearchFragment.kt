package org.projectPA.petdiary.view.fragment.community.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.testproject.ui.socialmedia.post.PostAdapter
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentUserSearchBinding
import org.projectPA.petdiary.view.adapters.UserAdapter
import org.projectPA.petdiary.viewmodel.PostViewModel
import org.projectPA.petdiary.viewmodel.UserViewModel

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentUserSearchBinding
    private lateinit var userAdapter: UserAdapter
    private lateinit var postAdapter: PostAdapter

    private val userViewModel: UserViewModel by navGraphViewModels(R.id.community_nav) { UserViewModel.Factory }
    private val postViewModel: PostViewModel by navGraphViewModels(R.id.community_nav) { PostViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserSearchBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userAdapter = UserAdapter(onClick = { user, _ ->
            userViewModel.setUser(user)
            findNavController().navigate(R.id.action_userSearchFragment_to_userProfileFragment)
        })

        binding.userRV.adapter = userAdapter

        userViewModel.users.observe(viewLifecycleOwner) { users ->
            userAdapter.submitList(users)
            binding.noUserTV.visibility = if (users.isEmpty()) View.VISIBLE else View.GONE
            binding.userRV.visibility = if (users.isEmpty()) View.GONE else View.VISIBLE
        }

        postAdapter = PostAdapter(onClick = { post, _ ->
            postViewModel.setPost(post)
            findNavController().navigate(R.id.action_userSearchFragment_to_communityCommentFragment)
        }, onLike = { post ->
            postViewModel.setLike(post.id ?: "")
        })

        binding.postRV.adapter = postAdapter

        postViewModel.posts.observe(viewLifecycleOwner) { posts ->
            postAdapter.submitList(posts)
            binding.noPostTV.visibility = if (posts.isEmpty()) View.VISIBLE else View.GONE
            binding.postRV.visibility = if (posts.isEmpty()) View.GONE else View.VISIBLE
        }

        userViewModel.loadUsers()
        postViewModel.loadPosts()


        binding.searchUserPostSV.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    userViewModel.searchUser(it)
                    postViewModel.searchPost(it)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (it.isEmpty()) {
                        userViewModel.loadUsers()
                        postViewModel.loadPosts()
                    } else {
                        userViewModel.searchUser(it)
                        postViewModel.searchPost(it)
                    }
                }
                return true
            }
        })

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}