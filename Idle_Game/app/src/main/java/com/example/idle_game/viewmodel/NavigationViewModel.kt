package com.example.idle_game.viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.idle_game.R
import com.example.idle_game.ui.fragments.HomeFragment
import com.example.idle_game.ui.fragments.InventoryFragment
import com.example.idle_game.ui.fragments.ScoreboardFragment

class NavigationViewModel: ViewModel() {
    private val _selectedFragment = MutableLiveData<Fragment>()
    val selectedFragment: LiveData<Fragment> get() = _selectedFragment

    private val fragments = mapOf(
        R.id.home to HomeFragment(),
        R.id.inventory to InventoryFragment(),
        R.id.scoreboard to ScoreboardFragment()
    )

    init {
        _selectedFragment.value = fragments[R.id.home]
    }

    fun selectFragment(menuItemId: Int) {
        _selectedFragment.value = fragments[menuItemId]
    }
}