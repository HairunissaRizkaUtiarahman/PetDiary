package org.projectPA.petdiary.view.fragment.myprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentCommentPostMyProfileFragmentBinding
import org.projectPA.petdiary.relativeTime
import org.projectPA.petdiary.view.adapters.CommentPostMyProfileAdapter
import org.projectPA.petdiary.viewmodel.CommentPostMyProfileViewModel
import org.projectPA.petdiary.viewmodel.PostMyProfileViewModel

class CommentPostMyProfileFragment : Fragment() {
    private lateinit var binding: FragmentCommentPostMyProfileFragmentBinding
    private lateinit var commentPostMyProfileAdapter: CommentPostMyProfileAdapter

    private val postMyProfileViewModel: PostMyProfileViewModel by navGraphViewModels(R.id.my_profile_nav)
    private val commentPostMyProfileViewModel: CommentPostMyProfileViewModel by viewModels { CommentPostMyProfileViewModel.Factory }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommentPostMyProfileFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        postMyProfileViewModel.myPost.observe(viewLifecycleOwner) {
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
            postMyProfileViewModel.myPost.value.let { post ->
                postMyProfileViewModel.setLike(post?.id ?: "").invokeOnCompletion {
                    postMyProfileViewModel.getPost(post?.id ?: "")
                }
            }
        }

        binding.unlikeBtn.setOnClickListener {
            postMyProfileViewModel.myPost.value.let { post ->
                postMyProfileViewModel.setLike(post?.id ?: "").invokeOnCompletion {
                    postMyProfileViewModel.getPost(post?.id ?: "")
                }
            }
        }

        binding.deleteBtn.setOnClickListener {
            showDeletePostConfirmationDialog()
        }

        commentPostMyProfileAdapter = CommentPostMyProfileAdapter(onDelete = { commentPost ->
            showDeleteCommentConfirmationDialog {
                commentPost.id?.let {
                    commentPostMyProfileViewModel.deleteComment(
                        postMyProfileViewModel.myPost.value?.id ?: "",
                        it
                    )
                }
            }
        }, currentUserId)

        binding.commentsRV.adapter = commentPostMyProfileAdapter

        commentPostMyProfileViewModel.commentsPost.observe(viewLifecycleOwner) { comments ->
            commentPostMyProfileAdapter.submitList(comments)
            if (comments.isEmpty()) {
                binding.noCommentTV.visibility = View.VISIBLE
                binding.commentsRV.visibility = View.GONE
            } else {
                binding.noCommentTV.visibility = View.GONE
                binding.commentsRV.visibility = View.VISIBLE
            }
            postMyProfileViewModel.updateCommentCount(comments.size)
        }

        commentPostMyProfileViewModel.loadData(postMyProfileViewModel.myPost.value?.id ?: "")

        binding.sendBtn.setOnClickListener {
            val comment = binding.commentTIET.text.toString().trim()

            if (comment != "") {
                commentPostMyProfileViewModel.uploadData(comment, postMyProfileViewModel.myPost.value?.id ?: "")
                Toast.makeText(requireContext(), "Success send comment", Toast.LENGTH_SHORT).show()
                binding.commentTIET.text?.clear()
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun showDeletePostConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        val postId = postMyProfileViewModel.myPost.value?.id ?: ""

        alertDialogBuilder.apply {
            setMessage("Are you sure you want to delete this post?")
            setPositiveButton("Yes") { _, _ ->
                postMyProfileViewModel.deleteData(postId)
                findNavController().popBackStack()
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        }
        alertDialogBuilder.create().show()
    }

    private fun showDeleteCommentConfirmationDialog(onConfirmedDelete: () -> Unit) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.apply {
            setMessage("Are you sure you want to delete this comment?")
            setPositiveButton("Yes") { _, _ ->
                onConfirmedDelete()
                Toast.makeText(requireContext(), "Comment deleted", Toast.LENGTH_SHORT).show()
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        }
        alertDialogBuilder.create().show()
    }
}