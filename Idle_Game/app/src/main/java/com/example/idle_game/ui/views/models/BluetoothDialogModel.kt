package com.example.idle_game.ui.views.models

import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.idle_game.data.repositories.BluetoothRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    private val bluetoothRepository: BluetoothRepository
) : ViewModel() {


    private val _bluetoothStatus = MutableStateFlow<BluetoothState>(BluetoothState.Checking)
    val bluetoothStatus: StateFlow<BluetoothState> = _bluetoothStatus

    private val _discoveredDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val discoveredDevices: StateFlow<List<BluetoothDevice>> = _discoveredDevices

    init {
        checkBluetoothStatus()
    }

    private fun checkBluetoothStatus() {
        if (!bluetoothRepository.isBluetoothEnabled()) {
            _bluetoothStatus.value = BluetoothState.Disabled
        } else {
            _bluetoothStatus.value = BluetoothState.Enabled
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
