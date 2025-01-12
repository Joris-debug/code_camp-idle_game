package com.example.idle_game.data.repositories

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class BluetoothRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val bluetooth = context.getSystemService(Context.BLUETOOTH_SERVICE)
            as? BluetoothManager
        ?: throw Exception("Bluetooth is not supported by this device")

    private val scanner: BluetoothAdapter?
        get() = bluetooth.adapter

    val discoveredDevices: MutableList<BluetoothDevice> = mutableListOf()

    fun isBluetoothEnabled(): Boolean {
        return bluetooth.adapter.isEnabled
    }

    private fun checkBluetoothPermissions(): Boolean {
        val bluetoothConnectPermission = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED

        val bluetoothScanPermission = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_SCAN
        ) == PackageManager.PERMISSION_GRANTED

        val bluetoothAdvertisePermission = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_ADVERTISE
        ) == PackageManager.PERMISSION_GRANTED

        return bluetoothConnectPermission && bluetoothScanPermission && bluetoothAdvertisePermission
    }

    private val foundDeviceReceiver = FoundDeviceReceiver { device ->
        if (!discoveredDevices.contains(device)) {
            discoveredDevices.add(device)
        }
    }

    @SuppressLint("MissingPermission")
    fun startScanning() {
        if(!checkBluetoothPermissions()) {
            return
        }

        context.registerReceiver(
            foundDeviceReceiver,
            IntentFilter(BluetoothDevice.ACTION_FOUND)
        )

        scanner?.startDiscovery()
    }

    // Asks the user to activate BT, if the device supports it and the permissions are granted
    // Only for Debugging, this should all be done on the viewmodel layer
    @SuppressLint("MissingPermission")
    fun startBluetoothConnection() {
        if (!isBluetoothEnabled()) {
            if (!checkBluetoothPermissions()) {
                Log.d("startBluetoothConnection:", "Missing permissions")
                return
            }
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(enableBtIntent)
        }
    }

    // Start scanning for nearby Bluetooth devices
    @SuppressLint("MissingPermission")
    fun startBluetoothScan() {
        if (!checkBluetoothPermissions()) {
            Log.d("startBluetoothScan:", "Missing Bluetooth permissions")
            return
        }

        if (!isBluetoothEnabled()) {
            Log.d("startBluetoothScan:", "Bluetooth is not enabled")
            return
        }

        stopScanning()
        startScanning()
    }

    // Stop Bluetooth discovery after scanning
    @SuppressLint("MissingPermission")
    fun stopScanning() {
        if (checkBluetoothPermissions() && bluetooth.adapter.isDiscovering) {
            scanner?.cancelDiscovery()
        }
    }

}