package org.projectPA.petdiary.view.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import org.projectPA.petdiary.view.fragment.myprofile.PostMyProfileFragment
import org.projectPA.petdiary.view.fragment.myprofile.ReviewMyProfileFragment

class MyProfileAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return 2 // Jumlah tab
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PostMyProfileFragment() // Fragment untuk tab "POST"
            1 -> ReviewMyProfileFragment() // Fragment untuk tab "REVIEW"
            else -> throw IllegalStateException("Invalid position $position")
        }
    }
}