package org.projectPA.petdiary.view.fragment.myprofile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentDetailReviewMyProfileBinding
import org.projectPA.petdiary.view.adapters.CommentReviewMyProfileAdapter
import org.projectPA.petdiary.viewmodel.ReviewMyProfileViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class DetailReviewMyProfileFragment : Fragment() {
    private lateinit var binding: FragmentDetailReviewMyProfileBinding
    private lateinit var commentReviewMyProfileAdapter: CommentReviewMyProfileAdapter

    private val viewModel: ReviewMyProfileViewModel by navGraphViewModels(R.id.my_profile_nav) { ReviewMyProfileViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailReviewMyProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Mengamati perubahan data pada ViewModel untuk ulasan pengguna
        viewModel.myReview.observe(viewLifecycleOwner) {
            with(binding) {
                descReviewTV.text = it.reviewText
                brandNameTV.text = it.product?.brandName
                productNameTV.text = it.product?.productName
                productTypeTV.text = it.product?.petType
                reviewDateTV.text =
                    it.timeReviewed?.toDate()?.let { it1 ->
                        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(it1)
                    }
                usagePeriodReviewTV.text = it.usagePeriod
                recomendedOrNotText.text =
                    if (it.rating >= 4) "I Recommend This Product" else "Not Recommended"
                ratingBar2.rating = it.rating
                nameTV.text = it.user?.name

                Glide.with(productImageIV.context).load(it.product?.imageUrl)
                    .placeholder(R.drawable.image_profile).into(productImageIV)

                Glide.with(profileImageIV.context).load(it.user?.imageUrl)
                    .placeholder(R.drawable.image_profile).into(profileImageIV)
            }
        }

        commentReviewMyProfileAdapter = CommentReviewMyProfileAdapter()
        binding.listComment.adapter = commentReviewMyProfileAdapter

        // Mengamati perubahan daftar komentar dari ViewModel
        viewModel.commentsReview.observe(viewLifecycleOwner) { comments ->
            commentReviewMyProfileAdapter.submitList(comments)
        }

        // Memuat komentar dari ViewModel berdasarkan id ulasan yang sedang dilihat
        viewModel.loadComment(viewModel.myReview.value?.id ?: "")

        // Tombol "Kirim" untuk mengirim komentar baru
        binding.sendBtn.setOnClickListener {
            val comment = binding.commentTIET.text.toString().trim()

            if (comment.isNotEmpty()) {
                viewModel.uploadComment(viewModel.myReview.value?.id ?: "", comment)
                Toast.makeText(requireContext(), "Success send comment", Toast.LENGTH_SHORT).show()
                binding.commentTIET.text?.clear()
            }
        }

        // Menangani aksi klik pada tombol "Lihat Semua Komentar" untuk menampilkan atau menyembunyikan daftar komentar
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

        // Tombol Back di TopAppBar untuk kembali ke stack sebelumnya
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}
