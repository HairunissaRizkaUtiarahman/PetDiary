package org.projectPA.petdiary.view.activities.myprofile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import org.projectPA.petdiary.R

class MyProfileActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.myProfileActivity) as NavHostFragment // Mendapatkan NavHostFragment dari layout
        navController =  navHostFragment.navController // Mendapatkan NavController dari NavHostFragment
    }
}