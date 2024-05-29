package org.projectPA.petdiary.view.fragment.community.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentPostUserProfileBinding
import org.projectPA.petdiary.view.adapters.PostUserProfileAdapter
import org.projectPA.petdiary.viewmodel.UserSearchViewModel


class PostUserProfileFragment : Fragment() {
    private lateinit var binding: FragmentPostUserProfileBinding
    private lateinit var adapter: PostUserProfileAdapter

    private val postUserProfileViewModel: PostUserProfileViewModel by navGraphViewModels(R.id.community_nav) { PostUserProfileViewModel.Factory }
    private val userSearchViewModel: UserSearchViewModel by navGraphViewModels(R.id.community_nav)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPostUserProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = PostUserProfileAdapter(onClick = { post, _ ->
            postUserProfileViewModel.setPost(post)

            findNavController().navigate(R.id.action_userProfileFragment_to_commentPostUserProfileFragment)
        }, onLike = { post ->
            postUserProfileViewModel.setLike(post.id ?: "")
        })

        binding.postRV.adapter = adapter

        postUserProfileViewModel.posts.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        postUserProfileViewModel.loadData(userSearchViewModel.user.value?.id ?: "")
    }
}