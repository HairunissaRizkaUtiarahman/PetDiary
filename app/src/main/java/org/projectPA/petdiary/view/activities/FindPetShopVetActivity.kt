package org.projectPA.petdiary.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import org.projectPA.petdiary.R

class FindPetShopVetActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_petshop_vet)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.findpetshopvetActivity) as NavHostFragment
        navController =  navHostFragment.navController
    }
}