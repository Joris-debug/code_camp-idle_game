package com.example.idle_game.ui.views.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.idle_game.ui.navigation.BottomBar
import com.example.idle_game.ui.navigation.NavigationGraph
import com.example.idle_game.ui.theme.Idle_GameTheme

@Composable
fun Idle_GameLauncher(modifier: Modifier = Modifier) {
    var isLoggedIn by remember { mutableStateOf(false) }

    if (isLoggedIn) {
        Idle_GameTheme {
            val navController = rememberNavController()
            Scaffold (
                modifier = Modifier.fillMaxSize(),
                bottomBar = { BottomBar(navController = navController) },
                content = { it ->
                    Row(modifier = Modifier.padding(it)) {
                        NavigationGraph(navController = navController)
                    }
                }
            )
        }
    } else {
        LoginView(hiltViewModel(), onLoginSuccess = { isLoggedIn = true })
    }
}