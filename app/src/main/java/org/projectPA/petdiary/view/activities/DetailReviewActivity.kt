package org.projectPA.petdiary.view.activities

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ActivityDetailReviewBinding
import org.projectPA.petdiary.model.CommentReview
import org.projectPA.petdiary.view.adapters.CommentReviewAdapter
import org.projectPA.petdiary.viewmodel.DetailReviewViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class DetailReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailReviewBinding
    private val viewModel: DetailReviewViewModel by viewModels()
    private lateinit var commentReviewAdapter: CommentReviewAdapter
    private lateinit var reviewId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productId = intent.getStringExtra("productId") ?: ""
        reviewId = intent.getStringExtra("reviewId") ?: ""

        if (productId.isNotEmpty() && reviewId.isNotEmpty()) {
            viewModel.fetchProductDetails(productId)
            viewModel.fetchReviewDetails(reviewId)
            viewModel.fetchCommentsCount(reviewId)
            viewModel.fetchCommentsForReview(reviewId)
        } else {
            Toast.makeText(this, "Invalid product or review ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        commentReviewAdapter = CommentReviewAdapter(emptyList(), viewModel.currentUserId) { comment ->
            Log.d("DetailReviewActivity", "Deleting comment: ${comment.id}")
            viewModel.deleteComment(comment)
        }
        binding.listComment.layoutManager = LinearLayoutManager(this)
        binding.listComment.adapter = commentReviewAdapter

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                Log.d("DetailReviewActivity", "Swiped to delete comment at position: $position")
                commentReviewAdapter.removeItem(position)
            }

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
                    val icon = ContextCompat.getDrawable(applicationContext, R.drawable.ic_delete)!!

                    c.drawRect(
                        itemView.right + dX,
                        itemView.top.toFloat(),
                        itemView.right.toFloat(),
                        itemView.bottom.toFloat(),
                        paint
                    )
                    val iconMargin = (itemView.height - icon.intrinsicHeight) / 2
                    icon.setBounds(
                        itemView.right - iconMargin - icon.intrinsicWidth,
                        itemView.top + iconMargin,
                        itemView.right - iconMargin,
                        itemView.bottom - iconMargin
                    )
                    icon.draw(c)
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.listComment)

    }

    @SuppressLint("SetTextI18n")
    private fun setupListeners() {
        binding.backToProductDetailButton.setOnClickListener {
            finish()
        }

        binding.viewAllCommentsButton.setOnClickListener {
            if (binding.listComment.visibility == View.VISIBLE) {
                binding.listComment.visibility = View.GONE
                binding.viewallTextview.text = "View"
                binding.commentCount.visibility = View.VISIBLE
                binding.commentTextview.text = "comment"
                binding.layoutCommentRL.visibility = View.GONE
            } else {
                binding.listComment.visibility = View.VISIBLE
                binding.viewallTextview.text = "Hide"
                binding.commentCount.visibility = View.GONE
                binding.commentTextview.text = "all comment"
                binding.layoutCommentRL.visibility = View.VISIBLE
            }
        }

        binding.sendBtn.setOnClickListener {
            val commentText = binding.commentTIET.text.toString()
            if (commentText.isNotBlank()) {
                val comment = CommentReview(
                    id = "",
                    reviewId = reviewId,
                    userId = viewModel.currentUserId,
                    commentText = commentText
                )
                viewModel.addComment(comment)
                binding.commentTIET.text?.clear()
                closeKeyboard()
            } else {
                Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun closeKeyboard() {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun observeViewModel() {
        viewModel.product.observe(this, Observer { product ->
            product?.let {
                binding.brandNameTV.text = it.brandName
                binding.productNameTV.text = it.productName
                binding.productTypeTV.text = it.petType

                Glide.with(this).load(it.imageUrl).into(binding.productImageIV)
            }
        })

        viewModel.review.observe(this, Observer { review ->
            review?.let {
                binding.reviewDate.text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(it.timeReviewed?.toDate())
                binding.deskripsiReview.text = it.reviewText
                binding.ratingBar2.rating = it.rating
                binding.usagePeriodReview.text = it.usagePeriod
                binding.recomendedOrNotText.text = if (it.recommend) "I Recommend This Product" else "Not Recommended"
            }
        })

        viewModel.user.observe(this, Observer { user ->
            user?.let {
                binding.nameTV.text = it.name
                val userPhotoUrl = user.imageUrl ?: ""

                if (userPhotoUrl.isEmpty()) {
                    binding.userPhotoProfile.setImageResource(R.drawable.ic_user)
                } else {
                    Glide.with(this).load(it.imageUrl).into(binding.userPhotoProfile)
                }
            }
        })

        viewModel.comments.observe(this, Observer { comments ->
            commentReviewAdapter.updateData(comments)
        })

        viewModel.commentsCount.observe(this, Observer { count ->
            binding.commentCount.text = count.toString()
            if (count == 0) {
                binding.layoutCommentRL.visibility = View.VISIBLE
                binding.viewAllCommentsButton.visibility = View.GONE
                binding.listComment.visibility = View.GONE
            } else {
                binding.layoutCommentRL.visibility = View.VISIBLE
                binding.viewallTextview.text = "Hide"
                binding.commentCount.visibility = View.GONE
                binding.commentTextview.text = "all comment"
                binding.viewAllCommentsButton.visibility = View.VISIBLE
                binding.listComment.visibility = View.VISIBLE
            }
        })

        viewModel.errorMessage.observe(this, Observer { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        })

        viewModel.commentAdded.observe(this, Observer { added ->
            if (added) {
                Log.d("DetailReviewActivity", "Comment added or deleted, refreshing comments")
                viewModel.fetchCommentsForReview(reviewId)
                viewModel.fetchCommentsCount(reviewId)
            }
        })



    }
}
