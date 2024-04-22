import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.projectPA.petdiary.databinding.FragmentHomeBinding
import org.projectPA.petdiary.ui.activities.ArticleActivity
import org.projectPA.petdiary.ui.activities.CommunityActivity
import org.projectPA.petdiary.ui.activities.FindPetshopVetActivity
import org.projectPA.petdiary.ui.activities.MyPetActivity
import org.projectPA.petdiary.ui.activities.ReviewHomePageActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        // Set onClick listeners for each button
        binding.managePetButton.setOnClickListener { openActivity(MyPetActivity::class.java) }
        binding.reviewButton.setOnClickListener { openActivity(ReviewHomePageActivity::class.java) }
        binding.findPetshopClinicButton.setOnClickListener { openActivity(FindPetshopVetActivity::class.java) }
        binding.communityButton.setOnClickListener { openActivity(CommunityActivity::class.java) }
        binding.articleButton.setOnClickListener { openActivity(ArticleActivity::class.java) }

        return view
    }

    private fun openActivity(cls: Class<*>) {
        val intent = Intent(activity, cls)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
