package com.example.idle_game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.idle_game.data.repositories.SettingsRepository
import com.example.idle_game.ui.theme.AppTheme
import com.example.idle_game.ui.views.composable.Idle_GameLauncher
import com.example.idle_game.ui.views.composable.LoadingScreenView
import com.example.idle_game.ui.views.composable.LoginView
import com.example.idle_game.util.SoundManager
import com.example.idle_game.util.checkAndRequestNotificationPermission
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var soundManager: SoundManager

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val contrast by settingsRepository.contrastState.collectAsState()
            val isDarkTheme by settingsRepository.themeState.collectAsState()
            AppTheme(darkTheme = isDarkTheme, contrast = contrast) {
                val navController = rememberNavController()

                val isLoggedIn =
                    remember { mutableStateOf<Boolean?>(null) } // True when logged in to an existing (in db) account
                val isWifiOK = remember { mutableStateOf(false) }
                val isSignedUp =
                    remember { mutableStateOf(false) }  // True when signed-up and logged-in

                if (isSignedUp.value || isLoggedIn.value == true) {
                    Idle_GameLauncher(navController = navController, soundManager)
                } else {
                    LoadingScreenView(
                        viewModel = hiltViewModel(),
                        onLoginSuccess = { isLoggedIn.value = true },
                        onLoginFailure = { isLoggedIn.value = false },
                        context = this,
                        onWifiOK = { isWifiOK.value = true }
                    )
                    if (isLoggedIn.value == false && isWifiOK.value) {
                        LoginView(
                            viewModel = hiltViewModel(),
                            onSignUpSuccess = { // Sign-up function is also calling login
                                isSignedUp.value = true
                                checkAndRequestNotificationPermission(this)
                            }
                        )
                    }
                }
            }
        }
    }
}