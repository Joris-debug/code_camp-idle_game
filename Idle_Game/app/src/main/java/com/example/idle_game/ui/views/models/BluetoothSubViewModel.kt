package com.example.idle_game.ui.views.models

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.idle_game.data.repositories.BluetoothRepository
import com.example.idle_game.data.repositories.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

sealed class BluetoothState {
    data object Checking : BluetoothState()
    data object Enabled : BluetoothState()
    data object Disabled : BluetoothState()
    data object Unavailable : BluetoothState()
    data class Error(val message: String) : BluetoothState()
    data class Devices(val devices: List<BluetoothDevice>) : BluetoothState()
}

@HiltViewModel
class BluetoothDialogModel @Inject constructor(
    private val bluetoothRepository: BluetoothRepository,
    val gameRepository: GameRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _bluetoothStatus = MutableStateFlow<BluetoothState>(BluetoothState.Checking)
    val bluetoothStatus: StateFlow<BluetoothState> = _bluetoothStatus

    private val _discoveredDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    var discoveredDevices: StateFlow<List<BluetoothDevice>> = _discoveredDevices

    private var bluetoothReceiver: BroadcastReceiver

    init {
        checkBluetoothStatus()
        bluetoothReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    BluetoothAdapter.ACTION_STATE_CHANGED -> {
                        val state =
                            intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                        _bluetoothStatus.value = when (state) {
                            BluetoothAdapter.STATE_ON -> BluetoothState.Enabled
                            BluetoothAdapter.STATE_OFF -> BluetoothState.Disabled
                            else -> BluetoothState.Error("Unbekannter Bluetooth-Status")
                        }
                    }
                }
            }
        }
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        context.registerReceiver(bluetoothReceiver, filter)

        bluetoothRepository.setOnPairedDevicesChanged { devices ->
            updateDiscoveredDevices(devices)
        }
    }

    override fun onCleared() {
        super.onCleared()
        context.unregisterReceiver(bluetoothReceiver)
    }

    private fun checkBluetoothStatus() {
        if (!bluetoothRepository.isBluetoothEnabled()) {
            _bluetoothStatus.value = BluetoothState.Disabled
        } else {
            _bluetoothStatus.value = BluetoothState.Enabled
        }
    }

    fun activateBluetoothConnection(onDismiss: () -> Unit) {
        bluetoothRepository.enableBluetoothConnection()
        viewModelScope.launch {
            delay(1000)
            if (_bluetoothStatus.value !is BluetoothState.Enabled && bluetoothRepository.isBluetoothEnabled()) {
                _bluetoothStatus.value = BluetoothState.Enabled
                onDismiss()
            } else {
                _bluetoothStatus.value = BluetoothState.Disabled
                onDismiss()
            }
        }
    }

    fun startScanning() {
        bluetoothRepository.startBluetoothScan()
    }

    fun listenOnSocketServer() {
        bluetoothRepository.enableBluetoothDiscoverability()
        viewModelScope.launch {
            bluetoothRepository.listenOnServerSocket()
        }
    }

    private fun connectToSelectedDevice(device: BluetoothDevice) {
        viewModelScope.launch {
            bluetoothRepository.connectFromClientSocket(device)
        }
    }

    private fun updateDiscoveredDevices(devices: List<BluetoothDevice>) {
        _bluetoothStatus.value = BluetoothState.Devices(devices)
        _discoveredDevices.value = devices
    }

    fun closeConnection() {
        bluetoothRepository.closeConnection()
        bluetoothRepository.cancelListenOnServerSocket()
        bluetoothRepository.stopScanning()
        _discoveredDevices.value = emptyList()
    }

    fun getBitcoinBalance(viewModel: StartViewModel): Long {
        return viewModel.viewState.value.coins.toLongOrNull() ?: 0L
    }

    private fun updateBitcoinBalance(amount: Long) {
        viewModelScope.launch {
            gameRepository.addBitcoins(amount)
        }
    }

    private fun read(): String {
        return runBlocking {
            val deferred = async { bluetoothRepository.read() }
            deferred.await()
        }
    }

    fun isConnected(): Boolean {
        return bluetoothRepository.isConnected()
    }

    fun sendBitcoin(amount: Long) {
        viewModelScope.launch {
            bluetoothRepository.write(amount.toString())
            var counter = 0
            while (!bluetoothRepository.isDataAvailable()) {
                if (counter >= MAX_WAIT_CYCLES) {
                    return@launch
                }
                delay(WAIT_TIME)
                counter++
            }
            val message = read()
            if (message == OK_MESSAGE) {
                updateBitcoinBalance(-amount)
            }
        }
    }

    suspend fun receiveBitcoin() {
        while (true) {
            if (bluetoothRepository.isConnected() && bluetoothRepository.isDataAvailable()) {
                val message = read()
                val longValue: Long = message.toLong()
                updateBitcoinBalance(longValue)
                bluetoothRepository.write(OK_MESSAGE)
                delay(WAIT_TIME)
                break
            }
            delay(WAIT_TIME)
        }
    }

    suspend fun initiateConnection(device: BluetoothDevice) {
        connectToSelectedDevice(device)
        withTimeout(WAIT_TIME * MAX_WAIT_CYCLES) {
            while (!isConnected()) {
                delay(WAIT_TIME)
            }
        }
    }

    companion object {
        const val OK_MESSAGE = "OK"
        const val WAIT_TIME = 100L
        const val MAX_WAIT_CYCLES = 50
    }
}