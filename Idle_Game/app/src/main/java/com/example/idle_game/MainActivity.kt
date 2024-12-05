package com.example.idle_game


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.idle_game.ui.views.composable.Idle_GameLauncher
import com.example.idle_game.ui.views.composable.LoginView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            val isLoggedIn = remember { mutableStateOf(false) }

            if (isLoggedIn.value) {
                Idle_GameLauncher(navController = navController, isLoggedIn = isLoggedIn)
            } else {
                // Andernfalls zeigen wir das LoginView an
                LoginView(viewModel = hiltViewModel(), onLoginSuccess = {
                    isLoggedIn.value = true
                })
            }
        }
    }
}


