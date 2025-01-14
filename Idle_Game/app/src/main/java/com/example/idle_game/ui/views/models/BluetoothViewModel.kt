package com.example.idle_game.ui.views.models

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.idle_game.data.repositories.BluetoothRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val bluetoothRepository: BluetoothRepository,
) : ViewModel() {

    var message: String = "No Message"

    fun activateBluetooth() = bluetoothRepository.enableBluetoothConnection()

    fun isBluetoothEnabled() = bluetoothRepository.isBluetoothEnabled()

    fun startBluetoothScan() = bluetoothRepository.startBluetoothScan()

    fun stopBluetoothScan() = bluetoothRepository.stopScanning()

    fun getDevices(): MutableSet<BluetoothDevice>  {
        return bluetoothRepository.discoveredDevices
    }

    fun enableDiscoverability() = bluetoothRepository.enableBluetoothDiscoverability()

    fun startBtServer() {
        viewModelScope.launch(Dispatchers.IO) {
            bluetoothRepository.listenOnServerSocket()
        }
    }

    @SuppressLint("MissingPermission")
    fun connectBtServer() {
        val myTablet = bluetoothRepository.getPairedDevices().firstOrNull { it.name == "Joris" }
        if (myTablet == null) {
            message = "Could not find device"
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            message = "connecting to server"
            bluetoothRepository.connectFromClientSocket(myTablet)
            message = "connection established"
        }
    }

    fun sendMessage() {
        viewModelScope.launch(Dispatchers.IO) {
            message = "sending"
            bluetoothRepository.write("Hello")
            message = "done sending"
        }
    }

    fun readMessage() {
        viewModelScope.launch(Dispatchers.IO) {
            message = "reading"
            message = bluetoothRepository.read()
        }
    }
}