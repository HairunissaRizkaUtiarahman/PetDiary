package org.projectPA.petdiary.view.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import org.projectPA.petdiary.view.fragment.myprofile.PetMyProfileFragment
import org.projectPA.petdiary.view.fragment.myprofile.PostMyProfileFragment
import org.projectPA.petdiary.view.fragment.myprofile.ReviewMyProfileFragment

class MyProfileTLAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PostMyProfileFragment()
            1 -> ReviewMyProfileFragment()
            2 -> PetMyProfileFragment()
            else -> throw IllegalStateException("Invalid position $position")
        }
    }
}