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
                        val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
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

        bluetoothRepository.onPairedDevicesChanged = { devices ->
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

    fun listenOnSocketServer(){
        bluetoothRepository.enableBluetoothDiscoverability()
        viewModelScope.launch {
            bluetoothRepository.listenOnServerSocket()
        }
    }

    fun connectToSelectedDevice(device: BluetoothDevice){
        viewModelScope.launch {
            bluetoothRepository.connectFromClientSocket(device)
        }
    }

    private fun updateDiscoveredDevices(devices: List<BluetoothDevice>) {
        _bluetoothStatus.value = BluetoothState.Devices(devices)
        _discoveredDevices.value = devices
        Log.e("Devices", _bluetoothStatus.value.toString())
    }

    fun closeConnection(){
        bluetoothRepository.closeConnection()
        bluetoothRepository.cancelListenOnServerSocket()
        bluetoothRepository.forgetAllPairedDevices()
        bluetoothRepository.stopScanning()
        _discoveredDevices.value = emptyList()
    }

    fun getBitcoinBalance(viewModel: StartViewModel): Long{
        return viewModel.viewState.value.coins.toLongOrNull() ?: 0L
    }

    fun updateBitcoinBalance(amount: Long){
        viewModelScope.launch {
            gameRepository.addBitcoins(amount)
        }
    }

    fun write(message: String){
        viewModelScope.launch {
            bluetoothRepository.write(message)
        }
    }

    fun isDataAvailable(): Boolean {
        return bluetoothRepository.isDataAvailable()
    }

    fun read(): String {
        return runBlocking {
            val deferred = async { bluetoothRepository.read() }
            deferred.await()
        }
    }

    fun isConnected(): Boolean{
        return bluetoothRepository.isConnected()
    }
}