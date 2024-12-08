package com.example.idle_game.ui.views.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.idle_game.data.database.models.InventoryData
import com.example.idle_game.data.repositories.GameRepository
import com.example.idle_game.data.workers.NotWorker
import com.example.idle_game.ui.views.states.StartViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {

//    class StartViewModel : HiltViewModel() {

    private val _viewState = MutableStateFlow(StartViewState())
    val viewState: StateFlow<StartViewState> get() = _viewState

    private fun getCoinsPerSecond(inventory: InventoryData): Int {
        return inventory.hackersLvl1 * 1 +
                inventory.hackersLvl2 * 2 +
                inventory.hackersLvl3 * 3 +
                inventory.hackersLvl4 * 4 +
                inventory.hackersLvl5 * 5 +
                inventory.botnetsLvl1 * 2 +
                inventory.botnetsLvl2 * 4 +
                inventory.botnetsLvl3 * 8 +
                inventory.botnetsLvl4 * 16 +
                inventory.botnetsLvl5 * 32 +
                inventory.cryptoMinersLvl1 * 3 +
                inventory.cryptoMinersLvl2 * 9 +
                inventory.cryptoMinersLvl3 * 27 +
                inventory.cryptoMinersLvl4 * 81 +
                inventory.cryptoMinersLvl5 * 243
    }

    private fun addCoins(newCoins: Int, inventory: InventoryData) {
        viewModelScope.launch {
            _viewState.value = _viewState.value.copy(
                coins = _viewState.value.coins + newCoins,
                isLoading = false,
                errorMessage = null,
                hackers = inventory.hackersLvl1 + inventory.hackersLvl2 + inventory.hackersLvl3 + inventory.hackersLvl4 + inventory.hackersLvl5,
                bots = inventory.botnetsLvl1 + inventory.botnetsLvl2 + inventory.botnetsLvl3 + inventory.botnetsLvl4 + inventory.botnetsLvl5,
                miners = inventory.cryptoMinersLvl1 + inventory.cryptoMinersLvl2 + inventory.cryptoMinersLvl3 + inventory.cryptoMinersLvl4 + inventory.cryptoMinersLvl5,
                coinsPerSec = newCoins
            )
            gameRepository.updateBitcoins(_viewState.value.coins)
        }

    }

    init {
        viewModelScope.launch { //Coroutine for passive income
            _viewState.value = _viewState.value.copy(coins = gameRepository.getInventory().bitcoins)
            val lastTimestamp = gameRepository.getLastTimestamp()
            if (lastTimestamp != null) {
                val duration = Duration.between(lastTimestamp, Instant.now())
                addCoins(
                    getCoinsPerSecond(gameRepository.getInventory()) * duration.seconds.toInt(),
                    gameRepository.getInventory()
                )
            }

            while (true) { //Stops with end of Coroutine Lifecycle
                val inventory = gameRepository.getInventory()
                val newCoins = getCoinsPerSecond(inventory)
                addCoins(newCoins, inventory)
                gameRepository.setTimestamp(Instant.now())
                delay(1000)
            }
        }
    }

    fun coinClick() {
        viewModelScope.launch {
            _viewState.value =
                _viewState.value.copy(activeBoost = gameRepository.getInventory().activeBoostType)
            //Todo: ist Booster noch aktiv?

            addCoins(1 + _viewState.value.activeBoost, gameRepository.getInventory())
        }
    }

//    fun incrementCoins(increment: Int, delayMinutes: Long, workManager: WorkManager) {
//        _viewState.value = _viewState.value.copy(
//            coins = _viewState.value.coins + increment,
//            isLoading = true,
//            errorMessage = null
//        )
//        scheduleNotWorker(delayMinutes, workManager)
//    }
//
//
//    private fun scheduleNotWorker(delayMinutes: Long, workManager: WorkManager) {
//        val workRequest: WorkRequest = OneTimeWorkRequest.Builder(NotWorker::class.java)
//            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
//            .build()
//
//        workManager.enqueue(workRequest)
//
//        _viewState.value = _viewState.value.copy(isLoading = false)
//    }

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

    //Test: Nur für tests
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
}
