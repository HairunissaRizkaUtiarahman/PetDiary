package org.projectPA.petdiary.view.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import org.projectPA.petdiary.R
import org.projectPA.petdiary.view.fragment.dashboard.HomeFragment
import org.projectPA.petdiary.view.fragment.dashboard.ProfileFragment
import org.projectPA.petdiary.viewmodel.DashboardViewModel
import org.projectPA.petdiary.databinding.ActivityDashboardBinding
import org.projectPA.petdiary.view.fragment.AddButtonFragment

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mengamati LiveData dari ViewModel untuk fragment yang dipilih
        viewModel.selectedFragment.observe(this, Observer { fragment ->
            replaceFragment(fragment)
        })

        // Mengatur fragment default yang akan ditampilkan
        viewModel.selectFragment(HomeFragment())

        // Tombol Navigasi di Buttom Bar
        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.home -> viewModel.selectFragment(HomeFragment())
                R.id.profile -> viewModel.selectFragment(ProfileFragment())
            }
            true
        }


        // Tombol "Add Button"
        binding.addButton.setOnClickListener {
            val fragment = AddButtonFragment()
            fragment.show(supportFragmentManager, fragment.tag)
        }
    }


    // Fungsi untuk mengganti fragment yang ditampilkan
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }
}