package org.projectPA.petdiary.view.fragment.community.search.user

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
import org.projectPA.petdiary.databinding.FragmentDetailReviewUserProfileBinding
import org.projectPA.petdiary.view.adapters.CommentReviewUserProfileAdapter
import org.projectPA.petdiary.viewmodel.ReviewUserProfileViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class DetailReviewUserProfileFragment : Fragment() {
    private lateinit var binding: FragmentDetailReviewUserProfileBinding
    private lateinit var adapter: CommentReviewUserProfileAdapter

    private val viewModel: ReviewUserProfileViewModel by navGraphViewModels(R.id.community_nav) { ReviewUserProfileViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailReviewUserProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Mengamati perubahan data pada ViewModel untuk ulasan pengguna
        viewModel.review.observe(viewLifecycleOwner) {
            with(binding) {
                descReviewTV.text = it.reviewText
                brandNameTV.text = it.product?.brandName
                productNameTV.text = it.product?.productName
                productTypeTV.text = it.product?.petType
                reviewDateTV.text =
                    it.timeReviewed?.toDate()?.let { date ->
                        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date)
                    }
                usagePeriodReviewTV.text = it.usagePeriod
                recomendedOrNotText.text =
                    if (it.rating >= 4) "I Recommend This Product" else "Not Recommended"
                ratingBar2.rating = it.rating
                nameTV.text = it.user?.name

                Glide.with(productImageIV.context).load(it.product?.imageUrl)
                    .placeholder(R.drawable.image_blank).into(productImageIV)
                Glide.with(profileImageIV.context).load(it.user?.imageUrl)
                    .placeholder(R.drawable.image_blank).into(profileImageIV)
            }
        }

        adapter = CommentReviewUserProfileAdapter()
        binding.commentsRV.adapter = adapter

        // Mengamati perubahan daftar komentar dari ViewModel
        viewModel.commentsReview.observe(viewLifecycleOwner) { comments ->
            adapter.submitList(comments)
        }

        // Memuat komentar dari ViewModel berdasarkan id ulasan yang sedang dilihat
        viewModel.loadComment(viewModel.review.value?.id ?: "")

        // Tombol "Kirim" untuk mengirim komentar baru
        binding.sendBtn.setOnClickListener {
            val comment = binding.commentTIET.text.toString().trim()

            if (comment.isNotEmpty()) {
                viewModel.uploadComment(viewModel.review.value?.id ?: "", comment)
                Toast.makeText(requireContext(), "Success send comment", Toast.LENGTH_SHORT).show()
                binding.commentTIET.text?.clear()
            }
        }

        // Tombol Back di TopAppBar untuk kembali ke stack sebelumnya
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}
