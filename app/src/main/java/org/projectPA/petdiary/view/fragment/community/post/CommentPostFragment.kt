package org.projectPA.petdiary.view.fragment.community.post

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostCommentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postViewModel.post.observe(viewLifecycleOwner) { post ->
            with(binding) {
                descTV.text = post.caption
                namePostTV.text = post.user?.name
                timestampTV.text = post.timePosted?.relativeTime() ?: ""

                Glide.with(profileImageIV.context).load(post.user?.imageUrl)
                    .placeholder(R.drawable.image_profile).into(profileImageIV)

                if (!post.imageUrl.isNullOrEmpty()) {
                    postImageIV.visibility = View.VISIBLE
                    Glide.with(postImageIV.context).load(post.imageUrl)
                        .placeholder(R.drawable.image_blank).into(postImageIV)
                }

                likeCountTV.text = requireContext().getString(R.string.like_count, post.likeCount)
                commentCountTV.text = requireContext().getString(R.string.comment_count, post.commentCount)
            }

            // Status like
            if (post.like != null) {
                binding.likeBtn.visibility = View.VISIBLE
                binding.unlikeBtn.visibility = View.GONE
            } else {
                binding.likeBtn.visibility = View.GONE
                binding.unlikeBtn.visibility = View.VISIBLE
            }
        }

        // Tombol Like
        binding.likeBtn.setOnClickListener {
            postViewModel.post.value?.let { post ->
                postViewModel.setLike(post.id ?: "").invokeOnCompletion {
                    postViewModel.getPost(post.id ?: "")
                }
            }
        }

        // Tombol Unlike
        binding.unlikeBtn.setOnClickListener {
            postViewModel.post.value?.let { post ->
                postViewModel.setLike(post.id ?: "").invokeOnCompletion {
                    postViewModel.getPost(post.id ?: "")
                }
            }
        }

        commentPostAdapter = CommentPostAdapter()

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
            postViewModel.updateCommentCount(comments.size)
        }

        // Load Komentar
        commentPostViewModel.loadData(postViewModel.post.value?.id ?: "")

        // Tombol Kirim Komentar
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
                val commentPost = commentPostAdapter.currentList[viewHolder.adapterPosition]
                return if (commentPost.userId == currentUserId) {
                    super.getSwipeDirs(recyclerView, viewHolder)
                } else {
                    0
                }
            }

            // Menangani swipe untuk menghapus komentar
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val commentPost = commentPostAdapter.currentList[position]
                commentPost.id?.let {
                    commentPostViewModel.deleteComment(postViewModel.post.value?.id ?: "", it)
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
}
