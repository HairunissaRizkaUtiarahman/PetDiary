package org.projectPA.petdiary.view.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
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
        commentReviewAdapter = CommentReviewAdapter(emptyList())
        binding.listComment.layoutManager = LinearLayoutManager(this)
        binding.listComment.adapter = commentReviewAdapter
    }

    @SuppressLint("SetTextI18n")
    private fun setupListeners() {
        binding.backToProductDetailButton.setOnClickListener {
            finish()
        }

        binding.viewAllCommentsButton.setOnClickListener {
            if (binding.listComment.visibility == View.VISIBLE) {
                binding.listComment.visibility = View.GONE
                binding.viewallTextview.text = "View all"
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
                binding.listComment.visibility = View.VISIBLE
            }
        })

        viewModel.errorMessage.observe(this, Observer { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        })

        viewModel.commentAdded.observe(this, Observer { added ->
            if (added) {
                viewModel.fetchCommentsForReview(reviewId)
                viewModel.fetchCommentsCount(reviewId)
            }
        })
    }
}
