package com.example.idle_game.ui.views.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.idle_game.ui.navigation.BottomBar
import com.example.idle_game.ui.navigation.NavigationGraph
import com.example.idle_game.util.SoundManager

@Composable
fun Idle_GameLauncher(navController: NavHostController, soundManager: SoundManager) {
    Scaffold(
        bottomBar = { BottomBar(navController, soundManager) }
    ) { innerPadding ->
        NavigationGraph(
            modifier = Modifier.padding(innerPadding),
            navController = navController
        )
    }
}