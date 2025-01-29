package com.example.idle_game

import android.app.Activity
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
import com.example.idle_game.util.SoundManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import android.Manifest
import androidx.annotation.RequiresApi

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var soundManager: SoundManager

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        checkAndRequestBluetoothPermissions(this)
        checkAndRequestNotificationPermission(this)
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                val navController = rememberNavController()

                val isLoggedIn = remember { mutableStateOf<Boolean?>(null) } // True when logged in to an existing (in db) account
                val isWifiOK = remember { mutableStateOf(false) }
                val isSignedUp = remember { mutableStateOf(false) }  // True when signed-up and logged-in

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
                            }
                        )
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun checkAndRequestBluetoothPermissions(activity: Activity) {
        val permissions = listOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION

        )

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(activity, permissionsToRequest.toTypedArray(), 1)
        }
    }

    fun checkAndRequestNotificationPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {  // Android 13 oder höher
            if (ContextCompat.checkSelfPermission(
                    activity,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {
                // Berechtigung noch nicht erteilt, daher anfordern
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1  // Anfragecode (kannst du nach Bedarf anpassen)
                )
            }
        }
    }
}