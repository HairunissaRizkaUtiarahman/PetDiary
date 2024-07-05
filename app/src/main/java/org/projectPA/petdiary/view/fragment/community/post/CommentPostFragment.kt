package org.projectPA.petdiary.view.fragment.community.post

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
import com.google.firebase.auth.FirebaseAuth
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentPostCommentBinding
import org.projectPA.petdiary.relativeTime
import org.projectPA.petdiary.view.adapters.CommentPostAdapter
import org.projectPA.petdiary.viewmodel.CommentPostViewModel
import org.projectPA.petdiary.viewmodel.PostViewModel

class CommentPostFragment : Fragment() {
    private lateinit var binding: FragmentPostCommentBinding
    private lateinit var commentPostAdapter: CommentPostAdapter

    private val postViewModel: PostViewModel by navGraphViewModels(R.id.community_nav)
    private val commentPostViewModel: CommentPostViewModel by viewModels { CommentPostViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostCommentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        postViewModel.post.observe(viewLifecycleOwner) {
            with(binding) {
                descTV.text = it.desc
                namePostTV.text = it.user?.name
                timestampTV.text = it.timePosted?.relativeTime() ?: ""

                Glide.with(profileImageIV.context).load(it.user?.imageUrl)
                    .placeholder(R.drawable.image_profile).into(profileImageIV)

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
            postViewModel.post.value.let { post ->
                postViewModel.setLike(post?.id ?: "").invokeOnCompletion {
                    postViewModel.getPost(post?.id ?: "")
                }
            }
        }

        binding.unlikeBtn.setOnClickListener {
            postViewModel.post.value.let { post ->
                postViewModel.setLike(post?.id ?: "").invokeOnCompletion {
                    postViewModel.getPost(post?.id ?: "")
                }
            }
        }

        commentPostAdapter = CommentPostAdapter({ commentPost ->
            commentPost.id?.let {
                commentPostViewModel.deleteComment(postViewModel.post.value?.id ?: "",
                    it
                )
            }
            Toast.makeText(requireContext(), "Comment deleted", Toast.LENGTH_SHORT).show()
        }, currentUserId)

        binding.commentsRV.adapter = commentPostAdapter

        commentPostViewModel.commentsPost.observe(viewLifecycleOwner) { comments ->
            commentPostAdapter.submitList(comments)
            if (comments.isEmpty()) {
                binding.noCommentTV.visibility = View.VISIBLE
                binding.commentsRV.visibility = View.GONE
            } else {
                binding.noCommentTV.visibility = View.GONE
                binding.commentsRV.visibility = View.VISIBLE
            }
        }

        commentPostViewModel.loadData(postViewModel.post.value?.id ?: "")

        binding.sendBtn.setOnClickListener {
            val commentText = binding.commentTIET.text.toString().trim()

            if (commentText.isEmpty() || commentText.length > 1000) {
                Toast.makeText(
                    requireContext(),
                    "Comment is required and must be less than 1000 characters",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            commentPostViewModel.uploadData(commentText, postViewModel.post.value?.id ?: "")
            Toast.makeText(requireContext(), "Success send comment", Toast.LENGTH_SHORT).show()

            binding.commentTIET.text?.clear()
        }

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}
