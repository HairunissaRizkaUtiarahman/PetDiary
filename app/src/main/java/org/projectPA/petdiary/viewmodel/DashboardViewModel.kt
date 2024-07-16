package org.projectPA.petdiary.viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    // LiveData privat untuk menyimpan fragment yang dipilih
    private val _selectedFragment = MutableLiveData<Fragment>()
    val selectedFragment: LiveData<Fragment> get() = _selectedFragment

    // Fungsi untuk memilih fragment yang akan ditampilkan
    fun selectFragment(fragment: Fragment) {
        _selectedFragment.value = fragment
    }
}
