package org.projectPA.petdiary.view.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ActivityDashboardBinding
import org.projectPA.petdiary.view.fragment.HomeFragment
import org.projectPA.petdiary.view.fragment.ProfileFragment
import org.projectPA.petdiary.viewmodel.DashboardViewModel

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Observe the selectedFragment LiveData from the ViewModel
        viewModel.selectedFragment.observe(this, Observer { fragment ->
            replaceFragment(fragment)
        })

        // Initialize the default fragment when the activity is created
        viewModel.selectFragment(HomeFragment())

        // Set listener for bottom navigation view
        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.home -> viewModel.selectFragment(HomeFragment())
                R.id.profile -> viewModel.selectFragment(ProfileFragment())
            }
            true // Return true to indicate that the item selection is handled
        }

        // Set click listener for add button
        binding.addButton.setOnClickListener {
            // Show the add button fragment with animation
            val fragment = AddButtonFragment()
            fragment.show(supportFragmentManager, fragment.tag)
        }
    }

    // Function to replace the current fragment with a new one
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }
}
