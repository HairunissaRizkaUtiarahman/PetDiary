package org.projectPA.petdiary.view.fragment.community.post

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.testproject.ui.socialmedia.post.PostAdapter
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentPostBinding
import org.projectPA.petdiary.view.activities.AddPostCommunityActivity
import org.projectPA.petdiary.view.activities.DashboardActivity
import org.projectPA.petdiary.viewmodel.PostViewModel

class PostFragment : Fragment() {
    private lateinit var binding: FragmentPostBinding
    private lateinit var adapter: PostAdapter

    private val viewModel: PostViewModel by navGraphViewModels(R.id.community_nav) { PostViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PostAdapter(onClick = { post, _ ->
            viewModel.setPost(post)
            findNavController().navigate(R.id.action_communityFragment_to_communityCommentFragment)
        }, onLike = { post ->
            viewModel.setLike(post.id ?: "")
        })

        binding.postRV.adapter = adapter

        // Observe posts LiveData
        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts)
            if (posts.isEmpty() || posts.all { it.isDeleted == true }) {
                // If no posts or all posts are deleted, show noPost_TV and hide postRV
                binding.noPostTV.visibility = View.VISIBLE
                binding.postRV.visibility = View.GONE
            } else {
                // If there are posts and some are not deleted, show postRV and hide noPost_TV
                binding.noPostTV.visibility = View.GONE
                binding.postRV.visibility = View.VISIBLE
            }
        }

        viewModel.loadRandomPosts()  // Call loadRandomPosts() instead of loadData()

        binding.addPostBtn.setOnClickListener {
            val intent = Intent(requireContext(), AddPostCommunityActivity::class.java)
            startActivity(intent)
        }

        binding.searchBtn.setOnClickListener {
            findNavController().navigate(R.id.action_communityFragment_to_userSearchFragment)
        }

        binding.backBtn.setOnClickListener {
            requireActivity().finish()
        }
    }
}
