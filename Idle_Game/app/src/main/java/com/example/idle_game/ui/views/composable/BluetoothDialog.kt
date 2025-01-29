package com.example.idle_game.ui.views.composable

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.idle_game.ui.views.models.BluetoothDialogModel
import com.example.idle_game.ui.views.models.BluetoothState
import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.clickable

@SuppressLint("StateFlowValueCalledInComposition", "MissingPermission")
@Composable
fun BluetoothDialog(
    viewModel: BluetoothDialogModel = hiltViewModel(),
    onDismiss: () -> Unit,
    onScan: () -> Unit, // Callback für den Scan-Button
    onDeviceSelected: (BluetoothDevice) -> Unit // Callback für die Gerätauswahl
) {
    val bluetoothState = viewModel.bluetoothStatus.value
    val discoveredDevices by viewModel.discoveredDevices.collectAsState()


    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Bluetooth Verbindung") },
        text = {
            Column {
                when (bluetoothState) {
                    is BluetoothState.Checking -> {
                        Text("Überprüfe den Bluetooth-Status...")
                    }
                    is BluetoothState.Enabled -> {
                        Text("Bluetooth ist aktiviert.")
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Möchtest du nach Geräten in der Nähe suchen?")
                        Spacer(modifier = Modifier.height(16.dp))
                        // Zeige die Liste der gefundenen Geräte, wenn verfügbar
                        if (discoveredDevices.isEmpty()) {
                            Text("Keine Geräte gefunden.")
                        } else {
                            LazyColumn {
                                items(discoveredDevices) { device ->
                                    Text(
                                        text = device.name ?: "Unbekannt",
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .clickable {
                                                onDeviceSelected(device)
                                            }
                                    )
                                }
                            }
                        }
                    }
                    is BluetoothState.Disabled -> {
                        Text("Bluetooth ist deaktiviert. Bitte aktiviere Bluetooth in den Smartphone Einstellungen, um fortzufahren.")
                    }
                    is BluetoothState.Unavailable -> {
                        Text("Dieses Gerät unterstützt kein Bluetooth.")
                    }
                    is BluetoothState.Error -> {
                        Text("Fehler: ${bluetoothState.message}")
                    }

                    is BluetoothState.Devices -> TODO()
                }
            }
        },
        confirmButton = {
            if (bluetoothState is BluetoothState.Enabled) {
                Button(onClick = onScan) { // Verwendet onScan, um den Scan zu starten
                    Text("Scannen")
                }
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Abbrechen")
            }
        }
    )
}
