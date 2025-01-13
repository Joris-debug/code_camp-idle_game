package com.example.idle_game.ui.views.models

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import com.example.idle_game.data.repositories.BluetoothRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val bluetoothRepository: BluetoothRepository,
) : ViewModel() {

    fun activateBluetooth() = bluetoothRepository.enableBluetoothConnection()

    fun isBluetoothEnabled() = bluetoothRepository.isBluetoothEnabled()

    fun startBluetoothScan() = bluetoothRepository.startBluetoothScan()

    fun stopBluetoothScan() = bluetoothRepository.stopScanning()

    fun getDevices(): MutableSet<BluetoothDevice>  {
        return bluetoothRepository.discoveredDevices
    }

    fun enableDiscoverability() = bluetoothRepository.enableBluetoothDiscoverability()
}