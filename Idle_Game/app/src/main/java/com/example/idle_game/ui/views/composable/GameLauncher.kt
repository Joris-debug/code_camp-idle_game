package com.example.idle_game.ui.views.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.idle_game.ui.navigation.BottomBar
import com.example.idle_game.ui.navigation.NavigationGraph
import com.example.idle_game.ui.theme.Idle_GameTheme

@Composable
fun Idle_GameLauncher(navController: NavHostController, isLoggedIn: MutableState<Boolean>) {
    if (isLoggedIn.value) {
        // Zeige das Spiel mit BottomBar und der Navigation an
        Scaffold(
            bottomBar = { BottomBar(navController) }
        ) { innerPadding ->
            NavigationGraph(
                modifier = Modifier.padding(innerPadding),
                navController = navController
            )
        }
    }
}