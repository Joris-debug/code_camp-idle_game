package com.example.idle_game

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.idle_game.ui.theme.AppTheme
import com.example.idle_game.ui.views.composable.Idle_GameLauncher
import com.example.idle_game.ui.views.composable.LoadingScreenView
import com.example.idle_game.ui.views.composable.LoginView
import dagger.hilt.android.AndroidEntryPoint
import android.Manifest

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val notificationPermissionRequestCode = 1001

    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    notificationPermissionRequestCode
                )
            }
        }

        setContent {
            AppTheme {
                val navController = rememberNavController()

                val isLoggedIn = remember { mutableStateOf<Boolean?>(null) } //true if logged in to an existing (in db) account
                val isWifiOK = remember { mutableStateOf(false) }
                val isSignedUp = remember { mutableStateOf(false) }  //ture if signed-up and logged-in

                if (isSignedUp.value || isLoggedIn.value == true) {
                    Idle_GameLauncher(navController = navController)
                } else {
                    LoadingScreenView(
                        viewModel = hiltViewModel(),
                        onLoginSuccess = { isLoggedIn.value = true },
                        onLoginFailure = { isLoggedIn.value = false },
                        context = this,
                        onWifiOK = { isWifiOK.value = true })
                    if (isLoggedIn.value == false && isWifiOK.value) {
                        LoginView(
                            viewModel = hiltViewModel(),
                            onSignUpSuccess = { //sign-up function is also calling login
                                isSignedUp.value = true
                            }
                        )
                    }
                }

            }
        }
    }
}


