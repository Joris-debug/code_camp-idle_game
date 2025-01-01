package com.example.idle_game.ui.views.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.idle_game.data.repositories.GameRepository
import com.example.idle_game.data.workers.NotWorker
import com.example.idle_game.ui.views.states.StartViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _viewState = MutableStateFlow(StartViewState())
    val viewState: StateFlow<StartViewState> get() = _viewState
    val inventoryFlow = gameRepository.inventoryDataFlow;

    private suspend fun getPassiveCoinsPerSecond(): Long {
        val inventory = inventoryFlow.first();
        val hacker = gameRepository.getHackerShopData()
        val miner = gameRepository.getMinerShopData()
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
            val inventory = inventoryFlow.first()
            _viewState.value = _viewState.value.copy(
                coins = _viewState.value.coins + newCoins,
                hackerLvl1 = inventory.hackersLvl1,
                hackerLvl2 = inventory.hackersLvl2,
                hackerLvl3 = inventory.hackersLvl3,
                hackerLvl4 = inventory.hackersLvl4,
                hackerLvl5 = inventory.hackersLvl5,
                minerLvl1 = inventory.cryptoMinersLvl1,
                minerLvl2 = inventory.cryptoMinersLvl2,
                minerLvl3 = inventory.cryptoMinersLvl3,
                minerLvl4 = inventory.cryptoMinersLvl4,
                minerLvl5 = inventory.cryptoMinersLvl5,
                botnetLvl1 = inventory.botnetsLvl1,
                botnetLvl2 = inventory.botnetsLvl2,
                botnetLvl3 = inventory.botnetsLvl3,
                botnetLvl4 = inventory.botnetsLvl4,
                botnetLvl5 = inventory.botnetsLvl5
            )
            gameRepository.addBitcoins(newCoins)
        }

    }

    init {
        viewModelScope.launch { //Coroutine for passive income
            gameRepository.updateShop()

            //Calculate earned Coins since last time using the app
            _viewState.value = _viewState.value.copy(coins = inventoryFlow.first().bitcoins)
            val lastTimestamp = gameRepository.getLastMiningTimestamp()
            if (lastTimestamp != null) {
                val duration = Duration.between(lastTimestamp, Instant.now())
                addCoins(getPassiveCoinsPerSecond() * duration.seconds)
            }

            while (true) { //Stops with end of Coroutine Lifecycle
                val inventory = inventoryFlow.first()
                val newCoins = getPassiveCoinsPerSecond()
                addCoins(newCoins)
                gameRepository.setMiningTimestamp(Instant.now())
                delay(1000)
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

    //TODO: Remove debug code before merge to main

    /*   Debug code start ------------------------------------------------------------------------------------------------*/
    fun addHacker() {
        viewModelScope.launch {
            gameRepository.addNewHacker()
        }
    }

    fun addBot() {
        viewModelScope.launch {
            gameRepository.addNewBotnet()
        }
    }

    fun addMiner() {
        viewModelScope.launch {
            gameRepository.addNewCryptoMiner()
        }
    }

    fun addBooster(lvl: Int) {
        viewModelScope.launch {
            when (lvl) {
                1 -> {
                    gameRepository.addLowBoost()
                    gameRepository.activateLowBoost()
                }

                2 -> {
                    gameRepository.addMediumBoost()
                    gameRepository.activateMediumBoost()
                }

                3 -> {
                    gameRepository.addHighBoost()
                    gameRepository.activateHighBoost()
                }
            }
        }
    }

    /*   Debug code end   ------------------------------------------------------------------------------------------------*/

}