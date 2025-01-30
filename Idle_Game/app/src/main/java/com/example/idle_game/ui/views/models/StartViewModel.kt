package com.example.idle_game.ui.views.models

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.idle_game.data.repositories.GameRepository
import com.example.idle_game.ui.views.states.StartViewState
import com.example.idle_game.util.SoundManager
import com.example.idle_game.util.shortBigNumbers
import com.example.idle_game.worker.NotificationWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
    val gameRepository: GameRepository,
    val soundManager: SoundManager,
    private val workManager: WorkManager
) : ViewModel() {

    private val _viewState = MutableStateFlow(StartViewState())
    val viewState: StateFlow<StartViewState> get() = _viewState
    private val inventoryFlow = gameRepository.getInventoryDataFlow()
    private var showShorted: Boolean = false
    private var coins: Long = 0
    private var coinsPerSec: Long = 0

    private fun toDisplay(value: Number): String {
        return if (showShorted) {
            shortBigNumbers(value.toLong())
        } else {
            value.toString()
        }
    }

    fun switchDisplayMode() {
        viewModelScope.launch {
            showShorted = !showShorted
            addCoins(0) //Just to set values in correct Display-Mode
            getPassiveCoinsPerSecond()
        }

    }

    private suspend fun getPassiveCoinsPerSecond(): Long {
        val inventory = inventoryFlow.first()
        val hacker = gameRepository.getHackerShopData()
        val miner = gameRepository.getCryptoMinerShopData()
        val botnet = gameRepository.getBotnetShopData()
        val multUpgrade2 = gameRepository.getUpgradeData(2)!!.multiplier!!
        val multUpgrade3 = gameRepository.getUpgradeData(3)!!.multiplier!!
        val multUpgrade4 = gameRepository.getUpgradeData(4)!!.multiplier!!
        val multUpgrade5 = gameRepository.getUpgradeData(5)!!.multiplier!!

        val passiveCoinsPerSec: Long = (inventory.hackersLvl1 * hacker.unitPerSec!! +
                inventory.hackersLvl2 * hacker.unitPerSec * multUpgrade2 +
                inventory.hackersLvl3 * hacker.unitPerSec * multUpgrade3 +
                inventory.hackersLvl4 * hacker.unitPerSec * multUpgrade4 +
                inventory.hackersLvl5 * hacker.unitPerSec * multUpgrade5 +
                inventory.cryptoMinersLvl1 * miner.unitPerSec!! +
                inventory.cryptoMinersLvl2 * miner.unitPerSec * multUpgrade2 +
                inventory.cryptoMinersLvl3 * miner.unitPerSec * multUpgrade3 +
                inventory.cryptoMinersLvl4 * miner.unitPerSec * multUpgrade4 +
                inventory.cryptoMinersLvl5 * miner.unitPerSec * multUpgrade5 +
                inventory.botnetsLvl1 * botnet.unitPerSec!! +
                inventory.botnetsLvl2 * botnet.unitPerSec * multUpgrade2 +
                inventory.botnetsLvl3 * botnet.unitPerSec * multUpgrade3 +
                inventory.botnetsLvl4 * botnet.unitPerSec * multUpgrade4 +
                inventory.botnetsLvl5 * botnet.unitPerSec * multUpgrade5
                ).toLong()
        _viewState.value = _viewState.value.copy(coinsPerSec = toDisplay(passiveCoinsPerSec))
        coinsPerSec = passiveCoinsPerSec

        return passiveCoinsPerSec
    }

    private fun addCoins(newCoins: Long) {
        viewModelScope.launch {
            val inventory = inventoryFlow.first()
            _viewState.value = _viewState.value.copy(
                coins = toDisplay(coins + newCoins),
                hackerCount = toDisplay(inventory.hackersLvl1 + inventory.hackersLvl2 + inventory.hackersLvl3 + inventory.hackersLvl4 + inventory.hackersLvl5),
                minerCount = toDisplay(inventory.cryptoMinersLvl1 + inventory.cryptoMinersLvl2 + inventory.cryptoMinersLvl3 + inventory.cryptoMinersLvl4 + inventory.cryptoMinersLvl5),
                botnetCount = toDisplay(inventory.botnetsLvl1 + inventory.botnetsLvl2 + inventory.botnetsLvl3 + inventory.botnetsLvl4 + inventory.botnetsLvl5)
            )
            coins += newCoins
            gameRepository.addBitcoins(newCoins)
        }
    }

    init {
        viewModelScope.launch { // Coroutine for passive income
            val millisPerSec: Long = 1000
            gameRepository.updateShop()
            _viewState.value =
                _viewState.value.copy(coins = toDisplay(inventoryFlow.first().bitcoins))
            scheduleNotificationWorker(workManager)

            while (true) { // Stops with end of coroutine lifecycle
                fetchBoost()
                coins = inventoryFlow.first().bitcoins
                val lastTimestamp = inventoryFlow.first().lastMiningTimestamp
                val duration = (System.currentTimeMillis() - lastTimestamp) / millisPerSec
                // ^ Get the duration in seconds ^
                /*
                * Why do I calculate the duration in every iteration?
                * A: The thread or even the entire app could freeze because of external factors,
                * which would lead to an incorrect coin count after responding again
                */
                gameRepository.setMiningTimestamp(lastTimestamp + duration * millisPerSec)
                // ^ Ensure the timestamp is incremented in 1-second intervals only ^
                addCoins(getPassiveCoinsPerSecond() * duration)
                delay(millisPerSec)
            }
        }
    }

    fun coinClick() {
        viewModelScope.launch {
            val clickedCoins: Long = 1 * let {
                if (gameRepository.isBoostActive()) {
                    gameRepository.getBoostFactor().toLong()
                } else {
                    1
                }
            }
            _viewState.value =
                _viewState.value.copy(
                    activeBoost = inventoryFlow.first().activeBoostType,
                    coinsPerSec = toDisplay(coinsPerSec + clickedCoins)
                )
            coinsPerSec += clickedCoins
            addCoins(clickedCoins)
        }
    }

    private fun scheduleNotificationWorker(workManager: WorkManager) {
        val periodicWorkRequest = PeriodicWorkRequest.Builder(NotificationWorker::class.java, 15, TimeUnit.MINUTES)
            .build()
        workManager.enqueue(periodicWorkRequest)
    }

    private suspend fun fetchBoost(){
        _viewState.value =
            _viewState.value.copy(
                activeBoost = inventoryFlow.first().activeBoostType,
                boostActiveUntil = inventoryFlow.first().boostActiveUntil
            )
        gameRepository.isBoostActive()
    }

    fun checkAndRequestBluetoothPermissions(activity: Activity) {
        val permissions = listOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION

        )

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(activity, permissionsToRequest.toTypedArray(), 1)
        }
    }
}