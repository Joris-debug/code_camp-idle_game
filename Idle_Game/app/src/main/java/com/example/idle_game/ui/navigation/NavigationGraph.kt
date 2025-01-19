package com.example.idle_game.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.idle_game.ui.views.composable.InventoryView
import com.example.idle_game.R
import com.example.idle_game.ui.views.composable.ScoreBoardView
import com.example.idle_game.ui.views.composable.StartView
import com.example.idle_game.ui.views.composable.BluetoothView
import com.example.idle_game.util.SoundManager

@Composable
fun NavigationGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = "StartView",
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable("StartView") {
            StartView()
        }
        composable("InventoryView") {
            InventoryView()
        }
        composable("ScoreBoardView") {
            ScoreBoardView()
        }
        composable("TestView") {
            BluetoothView()
        }
    }
}

@Composable
fun BottomBar(navController: NavController, soundManager: SoundManager) {
    BottomAppBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        NavigationBarItem(selected = currentRoute == "StartView", onClick = {
            soundManager.playSound(SoundManager.SWIPE_SOUND_RESOURCE_ID)
            navController.navigate("StartView") {
                navController.graph.startDestinationRoute?.let { screenRoute ->
                    popUpTo(screenRoute) {
                        saveState = false
                        inclusive = false
                    }
                }
                launchSingleTop = true
                restoreState = false }
        }, icon = { Icon(Icons.Default.Home, contentDescription = "Start")})

        NavigationBarItem(selected = currentRoute == "InventoryView", onClick = {
            soundManager.playSound(SoundManager.SWIPE_SOUND_RESOURCE_ID)
            navController.navigate("InventoryView") {
                navController.graph.startDestinationRoute?.let { screenRoute ->
                    popUpTo(screenRoute) {
                        saveState = false
                        inclusive = false
                    }
                }
                launchSingleTop = true
                restoreState = false }
        }, icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Inventory")})

        NavigationBarItem(selected = currentRoute == "ScoreBoardView", onClick = {
            soundManager.playSound(SoundManager.SWIPE_SOUND_RESOURCE_ID)
            navController.navigate("ScoreBoardView") {
                navController.graph.startDestinationRoute?.let { screenRoute ->
                    popUpTo(screenRoute) {
                        saveState = false
                        inclusive = false
                    }
                }
                launchSingleTop = true
                restoreState = false }
        }, icon = {
            val scoreboardIcon = painterResource(id = R.drawable.ic_scoreboard)
            Icon(scoreboardIcon, contentDescription = "Scoreboard")
        })

        NavigationBarItem(selected = currentRoute == "TestView", onClick = {
            navController.navigate("TestView") {
                navController.graph.startDestinationRoute?.let { screenRoute ->
                    popUpTo(screenRoute) {
                        saveState = false
                        inclusive = false
                    }
                }
                launchSingleTop = true
                restoreState = false }
        }, icon = { Icon(Icons.Default.Build, contentDescription = "Test")})
    }
}