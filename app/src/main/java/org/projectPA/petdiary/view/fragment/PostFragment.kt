package org.projectPA.petdiary.view.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import org.projectPA.petdiary.viewmodel.PostViewModel

private const val LOG_TAG = "PostFragment"

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
        adapter = PostAdapter(onClick = { post, _ ->
            viewModel.setPost(post)

            findNavController().navigate(R.id.action_communityFragment_to_communityCommentFragment)

        }, onLike = { post ->
            viewModel.setLike(post.id ?: "")
            Log.d(LOG_TAG, "Wishlist: ${post.desc} - ${post.like}")
        })

        binding.postRV.adapter = adapter

        viewModel.posts.observe(viewLifecycleOwner) {
            Log.d(LOG_TAG, "Post: $it")
            adapter.submitList(it)
        }
        viewModel.loadData()

        binding.addPostBtn.setOnClickListener {
            val intent = Intent(requireContext(), AddPostCommunityActivity::class.java)
            startActivity(intent)
        }
    }
}
