import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import org.projectPA.petdiary.R
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
                review.id?.let { viewModel.deleteReview(it) }
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
}

