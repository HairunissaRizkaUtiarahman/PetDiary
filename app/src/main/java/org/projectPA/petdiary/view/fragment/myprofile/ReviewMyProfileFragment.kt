import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.PackageManagerCompat.LOG_TAG
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.material.snackbar.Snackbar
import org.projectPA.petdiary.R
import org.projectPA.petdiary.SnackbarIdlingResource
import org.projectPA.petdiary.databinding.FragmentReviewMyProfileBinding
import org.projectPA.petdiary.view.adapters.ReviewMyProfileAdapter
import org.projectPA.petdiary.viewmodel.ReviewMyProfileViewModel

class ReviewMyProfileFragment : Fragment() {
    private lateinit var binding: FragmentReviewMyProfileBinding
    private lateinit var adapter: ReviewMyProfileAdapter

    private val viewModel: ReviewMyProfileViewModel by navGraphViewModels(R.id.my_profile_nav) { ReviewMyProfileViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReviewMyProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = ReviewMyProfileAdapter(
            onClick = { review, _ ->
                viewModel.setReview(review)
                findNavController().navigate(R.id.action_myProfileFragment_to_detailReviewMyProfileFragment)
            },
            onDeleteClick = { review ->
                review.id?.let { reviewId ->
                    review.productId?.let { productId ->

                        val alertDialogBuilder = AlertDialog.Builder(requireContext())
                        alertDialogBuilder.apply {
                            setMessage("Are you sure you want to delete this review?")
                            setPositiveButton("Yes") { _, _ ->
                                viewModel.deleteReview(reviewId, productId)
                                findNavController().popBackStack()
                                showSnackbar("Review deleted successfully")
                            }
                            setNegativeButton("No") { dialog, _ ->
                                dialog.dismiss()
                            }
                        }
                        alertDialogBuilder.create().show()

                    }
                }
            }
        )

        binding.myReviewRV.adapter = adapter

        viewModel.myReviews.observe(viewLifecycleOwner) { reviews ->
            adapter.submitList(reviews)
            binding.noReviewTV.visibility = if (reviews.isEmpty()) View.VISIBLE else View.GONE
            binding.myReviewRV.visibility = if (reviews.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.loadData()


    }

    private fun showSnackbar(message: String) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        SnackbarIdlingResource.SnackbarManager.registerSnackbar(snackbar)
        snackbar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar, event: Int) {
                SnackbarIdlingResource.SnackbarManager.unregisterSnackbar(snackbar)
            }
        })
        snackbar.show()
    }
}
