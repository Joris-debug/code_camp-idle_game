package com.example.idle_game.ui.views.models

import androidx.lifecycle.ViewModel
import com.example.idle_game.data.repositories.BluetoothRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TestViewModel @Inject constructor(
    private val bluetoothRepository: BluetoothRepository,
) : ViewModel() {

    fun activateBluetooth() = bluetoothRepository.startBluetoothConnection()

}