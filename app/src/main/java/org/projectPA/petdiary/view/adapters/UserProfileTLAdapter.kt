package org.projectPA.petdiary.view.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import org.projectPA.petdiary.view.fragment.community.search.user.PostUserProfileFragment
import org.projectPA.petdiary.view.fragment.community.search.user.ReviewUserProfileFragment
import org.projectPA.petdiary.viewmodel.UserViewModel

class UserProfileTLAdapter(fa: FragmentActivity, viewModel: UserViewModel) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return 2 // Number of tabs
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PostUserProfileFragment() // Fragment for the "POST" tab
            1 -> ReviewUserProfileFragment()
            else -> throw IllegalStateException("Invalid position $position")
        }
    }
}