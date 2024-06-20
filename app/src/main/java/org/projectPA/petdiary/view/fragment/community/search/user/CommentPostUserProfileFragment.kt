package org.projectPA.petdiary.view.fragment.community.search.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentCommentPostUserProfileBinding
import org.projectPA.petdiary.relativeTime
import org.projectPA.petdiary.view.adapters.CommentPostUserProfileAdapter
import org.projectPA.petdiary.viewmodel.CommentPostUserProfileViewModel
import org.projectPA.petdiary.viewmodel.PostUserProfileViewModel

class CommentPostUserProfileFragment : Fragment() {
    private lateinit var binding: FragmentCommentPostUserProfileBinding
    private lateinit var commentPostUserProfileAdapter: CommentPostUserProfileAdapter

    private val postUserProfileViewModel: PostUserProfileViewModel by navGraphViewModels(R.id.community_nav)
    private val commentPostUserProfileViewModel: CommentPostUserProfileViewModel by viewModels { CommentPostUserProfileViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommentPostUserProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postUserProfileViewModel.post.observe(viewLifecycleOwner) {
            with(binding) {
                descTV.text = it.desc
                namePostTV.text = it.user?.name
                timestampTV.text = it.timePosted?.relativeTime() ?: ""

                Glide.with(profileImageIV.context).load(it.user?.imageUrl)
                    .placeholder(R.drawable.image_blank).into(profileImageIV)

                if (it.imageUrl != "" && it.imageUrl != null) {
                    postImageIV.visibility = View.VISIBLE
                    Glide.with(postImageIV.context).load(it.imageUrl)
                        .placeholder(R.drawable.image_blank).into(postImageIV)
                }
                likeCountTV.text = requireContext().getString(R.string.like_count, it.likeCount)
                commentCountTV.text =
                    requireContext().getString(R.string.comment_count, it.commentCount)
            }
            if (it.like != null) {
                binding.likeBtn.visibility = View.VISIBLE
                binding.unlikeBtn.visibility = View.GONE
            } else {
                binding.unlikeBtn.visibility = View.VISIBLE
                binding.likeBtn.visibility = View.GONE
            }
        }
        binding.likeBtn.setOnClickListener {
            postUserProfileViewModel.post.value.let { post ->
                postUserProfileViewModel.setLike(post?.id ?: "").invokeOnCompletion {
                    postUserProfileViewModel.getPost(post?.id ?: "")
                }
            }
        }

        binding.unlikeBtn.setOnClickListener {
            postUserProfileViewModel.post.value.let { post ->
                postUserProfileViewModel.setLike(post?.id ?: "").invokeOnCompletion {
                    postUserProfileViewModel.getPost(post?.id ?: "")
                }
            }
        }

        commentPostUserProfileAdapter = CommentPostUserProfileAdapter()

        binding.commentsRV.adapter = commentPostUserProfileAdapter

        commentPostUserProfileViewModel.commentsPost.observe(viewLifecycleOwner) { comments ->
            commentPostUserProfileAdapter.submitList(comments)
            if (comments.isEmpty()) {
                binding.noCommentTV.visibility = View.VISIBLE
                binding.commentsRV.visibility = View.GONE
            } else {
                binding.noCommentTV.visibility = View.GONE
                binding.commentsRV.visibility = View.VISIBLE
            }
        }

        commentPostUserProfileViewModel.loadData(postUserProfileViewModel.post.value?.id ?: "")

        binding.sendBtn.setOnClickListener {
            val comment = binding.commentTIET.text.toString().trim()

            if (comment != "") {
                commentPostUserProfileViewModel.uploadData(
                    comment,
                    postUserProfileViewModel.post.value?.id ?: ""
                )
                Toast.makeText(requireContext(), "Success send comment", Toast.LENGTH_SHORT).show()
                binding.commentTIET.text?.clear()
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}