package com.example.idle_game


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.idle_game.ui.fragments.HomeFragment
import com.example.idle_game.ui.fragments.InventoryFragment
import com.example.idle_game.ui.fragments.ScoreboardFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private val homeFragment = HomeFragment()
    private val inventoryFragment = InventoryFragment()
    private val scoreboardFragment = ScoreboardFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        supportFragmentManager.beginTransaction().replace(R.id.container, homeFragment).commit()

        val badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.inventory)
        badgeDrawable.isVisible = true
        badgeDrawable.number = 8

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    Log.d("Navigation", "Home selected")
                    loadFragment(homeFragment)
                    true
                }
                R.id.inventory -> {
                    Log.d("Navigation", "Inventory selected")
                    loadFragment(inventoryFragment)
                    true
                }
                R.id.scoreboard -> {
                    Log.d("Navigation", "Scoreboard selected")
                    loadFragment(scoreboardFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
}