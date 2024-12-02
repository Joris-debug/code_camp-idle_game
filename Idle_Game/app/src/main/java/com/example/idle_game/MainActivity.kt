package com.example.idle_game


import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.idle_game.ui.navigation.BottomBar
import com.example.idle_game.ui.navigation.NavigationGraph
import com.example.idle_game.ui.views.composable.Idle_GameLauncher


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Idle_GameLauncher()
            val navController = rememberNavController()

            androidx.compose.material3.Scaffold(
                bottomBar = { BottomBar(navController) }
            ) { innerPadding ->
                NavigationGraph(
                    modifier = Modifier.padding(innerPadding),
                    navController = navController
                )
            }
        }
    }
}



