package org.projectPA.petdiary.viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    private val _selectedFragment = MutableLiveData<Fragment>()
    val selectedFragment: LiveData<Fragment> get() = _selectedFragment

    fun selectFragment(fragment: Fragment) {
        _selectedFragment.value = fragment
    }
}
