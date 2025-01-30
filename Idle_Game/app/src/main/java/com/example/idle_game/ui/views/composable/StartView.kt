package com.example.idle_game.ui.views.composable

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.idle_game.ui.views.models.StartViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.idle_game.R
import com.example.idle_game.ui.views.models.BluetoothDialogModel
import com.example.idle_game.util.SoundManager
import kotlinx.coroutines.delay


@SuppressLint("MissingPermission")
@Composable
fun StartView(
    viewModel: StartViewModel = hiltViewModel(),
    bluetoothDialogModel: BluetoothDialogModel = hiltViewModel()
) {
    val viewState = viewModel.viewState.collectAsState()
    var isClicked by remember { mutableStateOf(false) }
    var showBluetoothDialog by remember { mutableStateOf(false) }
    var showScanWindow by remember { mutableStateOf(false) }
    var showWaitingDialog by remember { mutableStateOf(false) }

    val onMessageReceived: (String) -> Unit = { message ->
        Log.e("Received Message", message)
    }

    val onDismiss: () -> Unit = {
        showWaitingDialog = false
    }

    // Scale-animation when coin is clicked
    val scale by animateFloatAsState(
        targetValue = if (isClicked) 1.05f else 1f,
        animationSpec = tween(durationMillis = 50), label = ""
    )
    LaunchedEffect(isClicked) {
        delay(50)
        isClicked = false
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.switchDisplayMode() }
                .background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(vertical = 15.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Coins: ${viewState.value.coins}",
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Bitcoins per Second: ${viewState.value.coinsPerSec}",
                    textAlign = TextAlign.Center
                )
            }
        }

        var remainingTime by remember { mutableLongStateOf(0L) }

        LaunchedEffect(viewState.value.boostActiveUntil) {
            while (true) {
                val currentTime = System.currentTimeMillis()
                remainingTime = (viewState.value.boostActiveUntil - currentTime).coerceAtLeast(0L)
                delay(1000L)
            }
        }

        val text = if (remainingTime > 0) {
            val hours = (remainingTime / (1000 * 60 * 60)) % 24
            val minutes = (remainingTime / (1000 * 60)) % 60
            val seconds = (remainingTime / 1000) % 60
            "%02d:%02d:%02d".format(hours, minutes, seconds)
        } else {
            "Kein Boost aktiv"
        }

        val boostIcon: Int? = when (viewState.value.activeBoost) {
            1 -> {
                R.drawable.low_boost
            }

            2 -> {
                R.drawable.medium_boost
            }

            3 -> {
                R.drawable.high_boost
            }

            else -> {
                null
            }
        }

        boostIcon?.let {
            PassiveBox(
                painterResource(id = it),
                "Information",
                text
            )
        }

        // Bluetooth Dialog Button
        Button(
            onClick = { showBluetoothDialog = true },
            modifier = Modifier
                .padding(16.dp)
                .background(Color.Blue),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
        ) {
            Text("Send BTC", color = Color.White)
        }

        // Bluetooth Dialog Display
        if (showBluetoothDialog) {
            BluetoothDialog(
                onDismiss = {
                    showBluetoothDialog = false
                    bluetoothDialogModel.closeConnection()
                            },
                onReceive = {
                    bluetoothDialogModel.listenOnSocketServer()
                    showWaitingDialog = true
                },
                onSend = {
                    showScanWindow = true
                }
            )
        }

        //Showing Window for scanning for devices
        if (showScanWindow) {
            ScanDialog(
                onDismiss = {
                    showScanWindow = false
                    bluetoothDialogModel.closeConnection()
                            },
                onScanClicked = {
                    bluetoothDialogModel.startScanning()
                }
            )
        }

        if (showWaitingDialog) {
            WaitingForRequestDialog(
                onMessageReceived = onMessageReceived,
                onDismiss = onDismiss
            )
        }

















        Image(
            painter = painterResource(id = R.drawable.bitcoin), "Klicken für Bitcoins",
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = {
                        viewModel.soundManager.playSound(SoundManager.CURSOR_SOUND_RESOURCE_ID)
                        viewModel.coinClick()
                        isClicked = !isClicked
                    }
                )
                .graphicsLayer(scaleX = scale, scaleY = scale)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
        )
        {
            Row(
                verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PassiveBox(
                    painterResource(id = R.drawable.hacker),
                    "Hacker",
                    viewState.value.hackerCount
                )
                PassiveBox(
                    painterResource(id = R.drawable.crypto_miner),
                    "Miner",
                    viewState.value.minerCount
                )
                PassiveBox(
                    painterResource(id = R.drawable.botnet),
                    "Botnet",
                    viewState.value.botnetCount
                )
            }
        }
    }
}

@Composable
fun PassiveBox(painter: Painter, description: String, count: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painter,
            description,
            modifier = Modifier,
        )
        Text(": $count", maxLines = 1)
    }
}





