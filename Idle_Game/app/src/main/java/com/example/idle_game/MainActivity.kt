package com.example.idle_game


import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.idle_game.viewmodel.NavigationViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private val navigationViewModel: NavigationViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        navigationViewModel.selectedFragment.observe(this, Observer { fragment ->
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        })

        bottomNavigationView.setOnItemSelectedListener { item ->
            navigationViewModel.selectFragment(item.itemId)
            true
        }

    }
}