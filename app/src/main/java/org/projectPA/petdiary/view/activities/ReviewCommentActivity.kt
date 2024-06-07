package org.projectPA.petdiary.view.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import org.projectPA.petdiary.databinding.ActivityReviewCommentBinding
import org.projectPA.petdiary.model.Comment
import org.projectPA.petdiary.view.adapters.CommentAdapter
import org.projectPA.petdiary.viewmodel.ReviewCommentViewModel

class ReviewCommentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewCommentBinding
    private val viewModel: ReviewCommentViewModel by viewModels()
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var reviewId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        reviewId = intent.getStringExtra("reviewId") ?: ""
        if (reviewId.isEmpty()) {
            Toast.makeText(this, "Invalid review ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }


        setupRecyclerView()
        setupListeners()
        observeViewModel()

        viewModel.fetchCommentsForReview(reviewId)

        binding.backToReviewDetailButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        commentAdapter = CommentAdapter(emptyList())
        binding.listComment.apply {
            layoutManager = LinearLayoutManager(this@ReviewCommentActivity)
            adapter = commentAdapter
        }
    }

    private fun setupListeners() {
        binding.sendBtn.setOnClickListener {
            val commentText = binding.commentTextInputEditText.text.toString()
            if (commentText.isNotEmpty()) {
                val comment = Comment(
                    reviewId = reviewId,
                    userId = viewModel.currentUserId,
                    text = commentText
                )
                viewModel.addComment(comment)
            } else {
                Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.comments.observe(this) { comments ->
            if (comments.isNullOrEmpty()) {
                binding.noCommentYetText.visibility = View.VISIBLE
            } else {
                binding.noCommentYetText.visibility = View.GONE
                commentAdapter.updateData(comments)
            }
        }

        viewModel.commentAdded.observe(this) {
            Toast.makeText(this, "Comment added", Toast.LENGTH_SHORT).show()
            binding.commentTextInputEditText.text?.clear()
            viewModel.fetchCommentsForReview(reviewId)
        }

        viewModel.errorMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }
}
