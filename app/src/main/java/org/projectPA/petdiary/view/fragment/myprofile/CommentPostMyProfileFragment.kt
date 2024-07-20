package org.projectPA.petdiary.view.fragment.myprofile

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
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
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommentPostMyProfileFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        postMyProfileViewModel.myPost.observe(viewLifecycleOwner) {
            with(binding) {
                descTV.text = it.caption
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
                commentCountTV.text = requireContext().getString(R.string.comment_count, it.commentCount)
            }

            // Status like
            if (it.like != null) {
                binding.likeBtn.visibility = View.VISIBLE
                binding.unlikeBtn.visibility = View.GONE
            } else {
                binding.unlikeBtn.visibility = View.VISIBLE
                binding.likeBtn.visibility = View.GONE
            }
        }

        // Tombol Like
        binding.likeBtn.setOnClickListener {
            postMyProfileViewModel.myPost.value.let { post ->
                postMyProfileViewModel.setLike(post?.id ?: "").invokeOnCompletion {
                    postMyProfileViewModel.getPost(post?.id ?: "")
                }
            }
        }

        // Tombol Unlike
        binding.unlikeBtn.setOnClickListener {
            postMyProfileViewModel.myPost.value.let { post ->
                postMyProfileViewModel.setLike(post?.id ?: "").invokeOnCompletion {
                    postMyProfileViewModel.getPost(post?.id ?: "")
                }
            }
        }

        // Tombol Delete Post
        binding.deleteBtn.setOnClickListener {
            showDeletePostConfirmationDialog()
        }

        commentPostMyProfileAdapter = CommentPostMyProfileAdapter()

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

        // Load Komentar
        commentPostMyProfileViewModel.loadData(postMyProfileViewModel.myPost.value?.id ?: "")

        // Tombol Kirim Komentar
        binding.sendBtn.setOnClickListener {
            val comment = binding.commentTIET.text.toString().trim()

            if (comment != "") {
                commentPostMyProfileViewModel.uploadData(comment, postMyProfileViewModel.myPost.value?.id ?: "")
                Toast.makeText(requireContext(), "Success send comment", Toast.LENGTH_SHORT).show()
                binding.commentTIET.text?.clear()
            }
        }

        // Tombol Back di Topbar
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

// Setup ItemTouchHelper untuk swipe delete
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT // Izinkan hanya swipe kiri
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun getSwipeDirs(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val commentPost = commentPostMyProfileAdapter.currentList[viewHolder.adapterPosition]
                return if (commentPost.userId == currentUserId) {
                    super.getSwipeDirs(recyclerView, viewHolder)
                } else {
                    0
                }
            }

            // Menangani swipe untuk menghapus komentar
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val commentPost = commentPostMyProfileAdapter.currentList[position]
                commentPost.id?.let {
                    commentPostMyProfileViewModel.deleteComment(postMyProfileViewModel.myPost.value?.id ?: "", it)
                    Toast.makeText(requireContext(), "Comment deleted", Toast.LENGTH_SHORT).show()
                }
            }

            // Menggambar latar belakang dan ikon saat swipe
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView = viewHolder.itemView
                    val paint = Paint().apply { color = Color.RED }
                    val icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete)!!

                    val iconMargin = (itemView.height - icon.intrinsicHeight) / 2
                    val iconTop = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
                    val iconBottom = iconTop + icon.intrinsicHeight

                    // Swipe ke kiri
                    val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
                    val iconRight = itemView.right - iconMargin
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)

                    c.drawRect(
                        itemView.right.toFloat() + dX,
                        itemView.top.toFloat(),
                        itemView.right.toFloat(),
                        itemView.bottom.toFloat(),
                        paint
                    )

                    icon.draw(c)
                }

                super.onChildDraw(
                    c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
                )
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.commentsRV)
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


}