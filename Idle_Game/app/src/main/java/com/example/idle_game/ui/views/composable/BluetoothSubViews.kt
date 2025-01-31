package com.example.idle_game.ui.views.composable

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.AlertDialog
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Button
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.CircularProgressIndicator
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.OutlinedTextField
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.idle_game.ui.views.models.BluetoothDialogModel
import com.example.idle_game.ui.views.models.BluetoothState
import com.example.idle_game.ui.views.models.StartViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

@SuppressLint("StateFlowValueCalledInComposition", "MissingPermission")
@Composable
fun BluetoothDialog(
    onDismiss: () -> Unit,
    bluetoothViewModel: BluetoothDialogModel = hiltViewModel(),
    onReceive: () -> Unit,
    onSend: () -> Unit
) {
    val bluetoothState = bluetoothViewModel.bluetoothStatus.value

    AlertDialog(onDismissRequest = onDismiss, title = { Text("Bluetooth Verbindung") }, text = {
        Column {
            when (bluetoothState) {
                is BluetoothState.Checking -> {
                    Text("Überprüfe den Bluetooth-Status...")
                }

                is BluetoothState.Enabled -> {
                    Text("Bluetooth ist aktiviert.")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Möchtest du BTC empfangen oder senden?")
                }

                is BluetoothState.Disabled -> {
                    bluetoothViewModel.activateBluetoothConnection(onDismiss = { onDismiss() })
                }

                is BluetoothState.Unavailable -> {
                    Text("Dieses Gerät unterstützt kein Bluetooth.")
                }

                is BluetoothState.Error -> {
                    Text("Fehler: ${bluetoothState.message}")
                }

                is BluetoothState.Devices -> {
                    Text("Bluetooth ist aktiviert.")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Möchtest du BTC empfangen oder senden?")
                }
            }
        }
    }, confirmButton = {
        Button(onClick = onSend) {
            Text("Senden")
        }

    }, dismissButton = {
        Button(onClick = onReceive) {
            Text("Empfangen")
        }
    })
}

//Dialog window for scan functionality
@SuppressLint("MissingPermission")
@Composable
fun ScanDialog(
    onDismiss: () -> Unit,
    onScanClicked: () -> Unit,
    bluetoothViewModel: BluetoothDialogModel = hiltViewModel(),
    viewModel: StartViewModel = hiltViewModel(),
) {
    val discoveredDevices by bluetoothViewModel.discoveredDevices.collectAsState()
    var showInputDialog by remember { mutableStateOf(false) }
    var maxBTC by remember { mutableLongStateOf(0L) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        title = {
            androidx.compose.material3.Text("Geräte scannen")
        },
        text = {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Gefundene Geräte:")
                Spacer(modifier = Modifier.height(16.dp))

                if (discoveredDevices.isEmpty() || discoveredDevices.all {it.name == null}) {
                    Text("Keine Geräte gefunden.")
                } else {
                    LazyColumn {
                        items(discoveredDevices) { device ->
                            Text(
                                text = device.name ?: "Unbekannt",
                                modifier = Modifier
                                    .padding(8.dp)
                                    .clickable(enabled = !isLoading) {
                                        isLoading = true
                                        CoroutineScope(Dispatchers.Main).launch {
                                            try {
                                                bluetoothViewModel.connectToSelectedDevice(device)

                                                withTimeout(10000) {
                                                    while (!bluetoothViewModel.isConnected()) {
                                                        delay(500)
                                                    }
                                                }
                                                maxBTC = bluetoothViewModel.getBitcoinBalance(viewModel)
                                                showInputDialog = true
                                            } catch (e: TimeoutCancellationException) {
                                                showErrorDialog = true
                                            } catch (e: Exception) {
                                                showErrorDialog = true
                                            } finally {
                                                isLoading = false

                                            }
                                        }
                                    })
                        }
                    }
                }

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .wrapContentSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable(enabled = false) {},
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(50.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                if (showErrorDialog) {
                    AlertDialog(
                        onDismissRequest = { showErrorDialog = false },
                        title = { Text("Fehler") },
                        text = { Text("Error: Versuche es später erneut.") },
                        confirmButton = {
                            Button(onClick = { showErrorDialog = false }) {
                                Text("OK")
                            }
                        }
                    )
                }

                BTCInputDialog(
                    isVisible = showInputDialog,
                    onDismiss = { showInputDialog = false },
                    maxBTC = maxBTC,
                    onSend = { amount ->
                        bluetoothViewModel.write(amount.toString())
                        bluetoothViewModel.updateBitcoinBalance(-amount)
                        showInputDialog = false
                    })

                Button(
                    onClick = onScanClicked,
                    enabled = !isLoading
                ) {
                    Text("Scan starten", color = Color.White)
                }
            }
        }, confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    bluetoothViewModel.closeConnection()
                          },
                enabled = !isLoading
            ) {
                Text("Abbrechen", color = Color.White)
            }
        })
}

@Composable
fun BTCInputDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    maxBTC: Long,
    onSend: (Long) -> Unit,
    bluetoothDialogModel: BluetoothDialogModel = hiltViewModel()
) {
    var inputAmount by remember { mutableStateOf("") }
    var btcAmount by remember { mutableLongStateOf(0L) }
    var showErrorDialog by remember { mutableStateOf(false) }

    if (isVisible) {
        AlertDialog(onDismissRequest = onDismiss, title = { Text("BTC senden") }, text = {
            Column {
                Text("Maximal verfügbar: $maxBTC BTC")
                OutlinedTextField(
                    value = inputAmount,
                    onValueChange = {
                        if (it.isEmpty() || it.toDoubleOrNull() != null && it.toDouble() <= maxBTC) {
                            inputAmount = it
                            btcAmount = (it.toDoubleOrNull() ?: 0.0).toLong()
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    keyboardActions = KeyboardActions.Default,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }, confirmButton = {
            Button(onClick = {
                if(bluetoothDialogModel.isConnected()) {
                    onSend(btcAmount)
                } else {
                    onDismiss()
                    showErrorDialog = true
                }
                bluetoothDialogModel.closeConnection()
            }) {
                Text("Senden")
            }
        }, dismissButton = {
            Button(onClick = {
                onDismiss()
                bluetoothDialogModel.closeConnection()
            }) {
                Text("Abbrechen", color = Color.White)
            }
        })
    }

    if(showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Fehler") },
            text = { Text("Error: Versuche es erneut.") },
            confirmButton = {
                Button(onClick = { showErrorDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun WaitingForRequestDialog(
    bluetoothViewModel: BluetoothDialogModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    CoroutineScope(Dispatchers.Main).launch {
        while (true) {
            if (bluetoothViewModel.isDataAvailable()) {
                val message = bluetoothViewModel.read()
                val longValue: Long = message.toLong()
                bluetoothViewModel.updateBitcoinBalance(longValue)
                onDismiss()
                bluetoothViewModel.closeConnection()
                break
            }
            delay(1000)
        }
    }

        AlertDialog(
            onDismissRequest = {
            onDismiss()
        },
            title = { Text("Warten auf Anfragen...") },
            text = { Text("Dein Gerät ist jetzt sichtbar und kann BTC empfangen.") },
            confirmButton = {
                Button(onClick = {
                    onDismiss()
                    bluetoothViewModel.closeConnection()
                }) {
                    Text("Abbrechen")
                }
            })
}