package com.example.idle_game.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.idle_game.ui.views.composable.StartView


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
        composable("TestView") {
            TestView()
        }
    }
}

@Composable
fun BottomBar(navController: NavController) {

    BottomAppBar (
        containerColor = Color.Gray,
        contentColor = Color.White
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        NavigationBarItem(selected = currentRoute == "StartView", onClick = {
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
        }, icon = { Icon(Icons.Default.Home, contentDescription = "Test")})
    }
}