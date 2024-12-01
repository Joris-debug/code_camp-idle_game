package com.example.idle_game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.idle_game.ui.navigation.BottomBar
import com.example.idle_game.ui.navigation.NavigationGraph
import com.example.idle_game.ui.theme.Idle_GameTheme
import com.example.idle_game.ui.views.composable.LoginView
import com.example.idle_game.ui.views.composable.StartView
import com.example.idle_game.ui.views.models.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
           Idle_GameLauncher()
            //LOGIN aufruf
            //LoginView(LoginViewModel())

            /* Idle_GameTheme {
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
            }*/
        }
    }
}

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
        LoginView(LoginViewModel(/*hilt + */onLoginSuccess = { isLoggedIn = true }), onLoginSuccess = { isLoggedIn = true })
    }
}
