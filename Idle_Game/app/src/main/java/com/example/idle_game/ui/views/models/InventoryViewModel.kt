package com.example.idle_game.ui.views.models
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.idle_game.data.database.models.InventoryData
import com.example.idle_game.data.database.models.ShopData
import com.example.idle_game.data.repositories.GameRepository
import com.example.idle_game.ui.views.states.InventoryViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

@HiltViewModel
class InventoryViewModel @Inject constructor(
    val gameRepository: GameRepository
) : ViewModel() {

    private val _viewState = MutableStateFlow(InventoryViewState())
    val viewState: StateFlow<InventoryViewState> = _viewState

    init {
        val millisPerSec: Long = 1000
        initState()
        viewModelScope.launch {
            while (true) {
                fetchActualBoost()
                fetchQuantityUpgrades()
                fetchQuantityProducer()
                delay(millisPerSec)
            }
        }
    }

    //Get amount of certain items
    fun getAmountOfItems(item: ShopData, inventoryData: InventoryData): Int {
        return when (item.name) {
            "low Boost" -> inventoryData.lowBoosts
            "medium Boost" -> inventoryData.mediumBoosts
            "high Boost" -> inventoryData.highBoosts
            "low passive" -> inventoryData.hackersLvl1 + inventoryData.hackersLvl2 + inventoryData.hackersLvl3 + inventoryData.hackersLvl4 + inventoryData.hackersLvl5
            "medium passive" -> inventoryData.cryptoMinersLvl1 + inventoryData.cryptoMinersLvl2 + inventoryData.cryptoMinersLvl3 + inventoryData.cryptoMinersLvl4 + inventoryData.cryptoMinersLvl5
            "high passive" -> inventoryData.botnetsLvl1 + inventoryData.botnetsLvl2 + inventoryData.botnetsLvl3 + inventoryData.botnetsLvl4 + inventoryData.botnetsLvl5
            "upgrade lvl 2" -> inventoryData.upgradeLvl2
            "upgrade lvl 3" -> inventoryData.upgradeLvl3
            "upgrade lvl 4" -> inventoryData.upgradeLvl4
            "upgrade lvl 5" -> inventoryData.upgradeLvl5
            else -> 0
        }
    }

    fun buyItem(itemToBuy: ShopData, amount: Int) {
        viewModelScope.launch {
            gameRepository.buyItem(itemToBuy, amount)
        }
    }

    fun useItem(itemToBuy: ShopData, useOn: String, quantity: Int) {
        viewModelScope.launch {
            gameRepository.useItem(itemToBuy, useOn, quantity)
        }
    }

    fun updateBitcoinBalance(cost: Long){
        viewModelScope.launch {
            gameRepository.issueBitcoins(cost)
        }
    }

    private fun initState(){
        viewModelScope.launch {
            gameRepository.updateShop()
            _viewState.value = _viewState.value.copy(shopData = gameRepository.getShopDataFlow())
            _viewState.value = _viewState.value.copy(inventoryData = gameRepository.getInventoryDataFlow())
        }
    }

    private suspend fun fetchActualBoost(){
        _viewState.value = _viewState.value.copy(activeBoost = gameRepository.getInventoryDataFlow().first().activeBoostType)
        gameRepository.isBoostActive()
    }

    private fun fetchQuantityUpgrades(){
        viewModelScope.launch {
            gameRepository.getInventoryDataFlow().collect { inventoryData ->
                _viewState.value = _viewState.value.copy(
                    amountUpgradeLvl2 = inventoryData.upgradeLvl2,
                    amountUpgradeLvl3 = inventoryData.upgradeLvl3,
                    amountUpgradeLvl4 = inventoryData.upgradeLvl4,
                    amountUpgradeLvl5 = inventoryData.upgradeLvl5
                )
            }
        }
    }

    private fun fetchQuantityProducer(){
        viewModelScope.launch {
            _viewState.value = _viewState.value.copy(
                amountHackerLvl1 = gameRepository.getInventoryDataFlow().first().hackersLvl1,
                amountHackerLvl2 = gameRepository.getInventoryDataFlow().first().hackersLvl2,
                amountHackerLvl3 = gameRepository.getInventoryDataFlow().first().hackersLvl3,
                amountHackerLvl4 = gameRepository.getInventoryDataFlow().first().hackersLvl4,

                amountMinerLvl1 = gameRepository.getInventoryDataFlow().first().cryptoMinersLvl1,
                amountMinerLvl2 = gameRepository.getInventoryDataFlow().first().cryptoMinersLvl2,
                amountMinerLvl3 = gameRepository.getInventoryDataFlow().first().cryptoMinersLvl3,
                amountMinerLvl4 = gameRepository.getInventoryDataFlow().first().cryptoMinersLvl4,

                amountBotNetLvl1 = gameRepository.getInventoryDataFlow().first().botnetsLvl1,
                amountBotNetLvl2 = gameRepository.getInventoryDataFlow().first().botnetsLvl2,
                amountBotNetLvl3 = gameRepository.getInventoryDataFlow().first().botnetsLvl3,
                amountBotNetLvl4 = gameRepository.getInventoryDataFlow().first().botnetsLvl4,
            )
        }
    }
}