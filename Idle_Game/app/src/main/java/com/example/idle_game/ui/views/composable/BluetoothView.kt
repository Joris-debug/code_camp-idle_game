package com.example.idle_game.ui.views.composable

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.idle_game.ui.views.models.BluetoothViewModel

@Composable
fun BluetoothView(viewModel: BluetoothViewModel = hiltViewModel()) {
    val scrollState = rememberScrollState()
    val permissionGranted = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissionGranted.value = permissions.all { it.value }
    }
    val permissionsToRequest =
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT
        )

    Column(modifier = Modifier.verticalScroll(scrollState)) {
        Spacer(modifier = Modifier.height(100.dp))
        Text(
            text = "Welcome on the test-page!",
            modifier = Modifier.padding(16.dp)
        )

        Button(
            onClick = {
                val permissionsNeeded = permissionsToRequest.filter {
                    ContextCompat.checkSelfPermission(
                        context,
                        it
                    ) != PackageManager.PERMISSION_GRANTED
                }

                if (permissionsNeeded.isEmpty()) {
                    permissionGranted.value = true
                } else {
                    launcher.launch(permissionsNeeded.toTypedArray())
                }
                if (!viewModel.isBluetoothEnabled()) {
                    viewModel.activateBluetooth()
                }
            }
        ) {
            Text(text = "Request Bluetooth Permissions")
        }
        Button(
            onClick = { viewModel.enableDiscoverability() }
        ) {
            Text(text = "Enable discoverability")
        }

        Button(
            onClick = { viewModel.startBluetoothScan() }
        ) {
            Text(text = "Start Bluetooth Scan")
        }
        Button(
            onClick = { viewModel.stopBluetoothScan() }
        ) {
            Text(text = "Stop Bluetooth Scan")
        }

        Button(
            onClick = {
                val devices = viewModel.getDevices()

                val deviceListMessage = if (devices.isEmpty()) {
                    "Keine Geräte gefunden."
                } else {
                    devices.joinToString(separator = "\n") { device ->
                        "Name: ${device.name ?: "Unbekannt"}, Adresse: ${device.address}"
                    }
                }

                AlertDialog.Builder(context)
                    .setTitle("Gefundene Geräte")
                    .setMessage(deviceListMessage)
                    .setPositiveButton("OK", null)
                    .show()
            }
        ) {
            Text(
                text = "Display devices"
            )
        }
    }

}
