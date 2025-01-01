package com.example.idle_game.ui.views.models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.idle_game.data.repositories.GameRepository
import com.example.idle_game.data.repositories.GameRepository.Companion.HIGH_BOOST_ID
import com.example.idle_game.data.repositories.GameRepository.Companion.LOW_BOOST_ID
import com.example.idle_game.data.repositories.GameRepository.Companion.MEDIUM_BOOST_ID
import com.example.idle_game.data.workers.NotWorker
import com.example.idle_game.ui.views.states.StartViewState
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
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _viewState = MutableStateFlow(StartViewState())
    val viewState: StateFlow<StartViewState> get() = _viewState
    private val inventoryFlow = gameRepository.getInventoryDataFlow()

    private suspend fun getPassiveCoinsPerSecond(): Long {
        val inventory = inventoryFlow.first();
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
        _viewState.value = _viewState.value.copy(coinsPerSec = passiveCoinsPerSec)

        return passiveCoinsPerSec
    }

    private fun addCoins(newCoins: Long) {
        viewModelScope.launch {
            val inv = inventoryFlow.first()
            gameRepository.addBitcoins(newCoins)
            _viewState.value = _viewState.value.copy(
                coins = gameRepository.getInventoryDataFlow().first().bitcoins,
                isLoading = false,
                errorMessage = null,
                hackers = inv.hackersLvl1 + inv.hackersLvl2 + inv.hackersLvl3 + inv.hackersLvl4 + inv.hackersLvl5,
                bots = inv.botnetsLvl1 + inv.botnetsLvl2 + inv.botnetsLvl3 + inv.botnetsLvl4 + inv.botnetsLvl5,
                miners = inv.cryptoMinersLvl1 + inv.cryptoMinersLvl2 + inv.cryptoMinersLvl3 + inv.cryptoMinersLvl4 + inv.cryptoMinersLvl5,
            )
        }
    }

    init {
        viewModelScope.launch { // Coroutine for passive income
            val millisPerSec: Long = 1000
            gameRepository.updateShop()
            _viewState.value = _viewState.value.copy(coins = inventoryFlow.first().bitcoins)

            while (true) { // Stops with end of coroutine lifecycle
                val lastTimestamp = inventoryFlow.first().lastMiningTimestamp
                gameRepository.setMiningTimestamp(System.currentTimeMillis())
                val duration = (System.currentTimeMillis() - lastTimestamp) / millisPerSec
                // ^ Get the duration in seconds ^
                /*
                * Why do I calculate the duration in every iteration?
                * A: The thread or even the entire app could freeze because of external factors,
                * which would lead to an incorrect coin count after responding again
                */
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
                    coinsPerSec = _viewState.value.coinsPerSec + clickedCoins
                )
            addCoins(clickedCoins)
        }
    }

    private fun scheduleNotWorker(delayMinutes: Long, workManager: WorkManager) {
        val workRequest: WorkRequest = OneTimeWorkRequest.Builder(NotWorker::class.java)
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .build()

        workManager.enqueue(workRequest)
    }
}