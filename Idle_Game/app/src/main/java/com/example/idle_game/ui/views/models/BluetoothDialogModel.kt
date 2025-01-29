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
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class BluetoothState {
    object Checking : BluetoothState()
    object Enabled : BluetoothState()
    object Disabled : BluetoothState()
    object Unavailable : BluetoothState()
    data class Error(val message: String) : BluetoothState()
    data class Devices(val devices: List<BluetoothDevice>) : BluetoothState()
}

@HiltViewModel
class BluetoothDialogModel @Inject constructor(
    private val bluetoothRepository: BluetoothRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {


    private val _bluetoothStatus = MutableStateFlow<BluetoothState>(BluetoothState.Checking)
    val bluetoothStatus: StateFlow<BluetoothState> = _bluetoothStatus

    private val _discoveredDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val discoveredDevices: StateFlow<List<BluetoothDevice>> = _discoveredDevices

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

        // Receiver registrieren
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        context.registerReceiver(bluetoothReceiver, filter)
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

    fun activateBluetoothConnection(onSuccess: () -> Unit){
        bluetoothRepository.enableBluetoothConnection()
        _bluetoothStatus.value = BluetoothState.Enabled
        viewModelScope.launch {
            delay(2000)
            if (_bluetoothStatus.value == BluetoothState.Enabled) {
                onSuccess()
            }
        }
    }

    fun startScanning() {
        bluetoothRepository.startBluetoothScan()
        updateDiscoveredDevices()
    }

    fun stopScanning() {
        bluetoothRepository.stopScanning()
    }

    private fun updateDiscoveredDevices() {
        val devices = bluetoothRepository.getDiscoveredDevices().toList()
        _bluetoothStatus.value = BluetoothState.Devices(devices)
        Log.e("Divices", bluetoothStatus.value.toString())
    }
}
