package org.projectPA.petdiary.view.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import org.projectPA.petdiary.view.fragment.PostUserProfileFragment
import org.projectPA.petdiary.view.fragment.ReviewUserProfileFragment

class UserProfileTLAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return 2 // Jumlah tab
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PostUserProfileFragment() // Fragment untuk tab "POST"
            1 -> ReviewUserProfileFragment() // Fragment untuk tab "REVIEW"
            else -> throw IllegalStateException("Invalid position $position")
        }
    }
}