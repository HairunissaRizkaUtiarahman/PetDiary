package org.projectPA.petdiary.ui.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ActivityDashboardBinding
import org.projectPA.petdiary.fragment.HomeFragment
import org.projectPA.petdiary.fragment.ProfileFragment

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the default fragment when the activity is created
        replaceFragment(HomeFragment())

        // Set listener for bottom navigation view
        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.home -> replaceFragment(HomeFragment())
                R.id.profile -> replaceFragment(ProfileFragment())
            }
            true // Return true to indicate that the item selection is handled
        }

        // Set click listener for add button
        binding.addButton.setOnClickListener {
            // Show the add button fragment with animation
            val fragment = FragmentAddButton()
            fragment.show(supportFragmentManager, fragment.tag)
        }
    }

    // Function to replace the current fragment with a new one
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }

    // FragmentAddButton untuk menampilkan dialog fragment
    class FragmentAddButton : BottomSheetDialogFragment() {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.fragment_add_button, container, false)
        }
    }
}
