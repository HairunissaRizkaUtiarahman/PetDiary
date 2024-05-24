package org.projectPA.petdiary.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentPostBinding
import org.projectPA.petdiary.view.adapters.PostAdapter
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
        adapter = PostAdapter(onClick = { post, _ ->
            viewModel.setPost(post)

            findNavController().navigate(R.id.action_communityFragment_to_communityCommentFragment)

        }, onLike = { post ->
            viewModel.setLike(post.id ?: "")
        })

        binding.postRV.adapter = adapter

        viewModel.posts.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.loadData()
    }
}
