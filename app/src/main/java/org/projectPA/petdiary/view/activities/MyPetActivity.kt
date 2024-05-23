package org.projectPA.petdiary.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import org.projectPA.petdiary.R

class MyPetActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypet)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.myPetAcivity) as NavHostFragment
        navController =  navHostFragment.navController
    }
}