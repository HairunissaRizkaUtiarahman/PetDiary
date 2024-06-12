package org.projectPA.petdiary.view.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
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

    private fun setupListeners() {
        binding.backToProductDetailButton.setOnClickListener {
            finish()
        }

        binding.viewAllCommentsButton.setOnClickListener {
            if (binding.listComment.visibility == View.VISIBLE) {
                binding.listComment.visibility = View.GONE
                binding.layoutCommentRL.visibility = View.GONE
            } else {
                binding.listComment.visibility = View.VISIBLE
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
                    text = commentText
                )
                viewModel.addComment(comment)
                binding.commentTIET.text?.clear()
            } else {
                Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show()
            }
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
                binding.reviewDate.text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(it.reviewDate?.toDate())
                binding.deskripsiReview.text = it.reviewText
                binding.ratingBar2.rating = it.rating
                binding.usagePeriodReview.text = it.usagePeriod
                binding.recomendedOrNotText.text = if (it.recommend) "I Recommend This Product" else "Not Recommended"
            }
        })

        viewModel.user.observe(this, Observer { user ->
            user?.let {
                binding.nameTV.text = it.name
                Glide.with(this).load(it.imageUrl).into(binding.userPhotoProfile)
            }
        })

        viewModel.comments.observe(this, Observer { comments ->
            commentReviewAdapter.updateData(comments)
        })

        viewModel.commentsCount.observe(this, Observer { count ->
            binding.commentCount.text = count.toString()
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
