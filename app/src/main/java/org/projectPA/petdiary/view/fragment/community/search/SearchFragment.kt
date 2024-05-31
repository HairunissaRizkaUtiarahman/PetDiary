package org.projectPA.petdiary.view.fragment.community.search

import android.os.Bundle
import android.util.Log
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
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserSearchBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Load User
        userAdapter = UserAdapter(onClick = { user, _ ->
            userViewModel.setUser(user)
            Log.d("UserSearchFragment", user.toString())
            Log.d("UserSearchFragment", userViewModel.user.value.toString())

            findNavController().navigate(R.id.action_userSearchFragment_to_userProfileFragment)
        })

        binding.userRV.adapter = userAdapter

        userViewModel.users.observe(viewLifecycleOwner) { users ->
            userAdapter.submitList(users)
            if (users.isEmpty()) {
                binding.noUserTV.visibility = View.VISIBLE
                binding.userRV.visibility = View.GONE
            } else {
                binding.noUserTV.visibility = View.GONE
                binding.userRV.visibility = View.VISIBLE
            }
        }

        userViewModel.loadData()

        // Load Post
        postAdapter = PostAdapter(onClick = { post, _ ->
            postViewModel.setPost(post)

            findNavController().navigate(R.id.action_userSearchFragment_to_communityCommentFragment)
        }, onLike = { post ->
            postViewModel.setLike(post.id ?: "")
        })

        binding.postRV.adapter = postAdapter

        postViewModel.posts.observe(viewLifecycleOwner) { posts ->
            postAdapter.submitList(posts)
            if (posts.isEmpty()) {
                binding.noPostTV.visibility = View.VISIBLE
                binding.postRV.visibility = View.GONE
            } else {
                binding.noPostTV.visibility = View.GONE
                binding.postRV.visibility = View.VISIBLE
            }
        }

        postViewModel.loadData()

        // Search User & Post
        binding.searchUserPostSV.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null && query.isNotEmpty()) {
                    userViewModel.searchUser(query)
                    postViewModel.searchPost(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    userViewModel.loadData()
                    postViewModel.loadData()
                } else {
                    userViewModel.searchUser(newText)
                    postViewModel.searchPost(newText)
                }
                return true
            }
        })
    }
}