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

@HiltViewModel
class InventoryViewModel @Inject constructor(
    val gameRepository: GameRepository
) : ViewModel() {

    private val _uiStateFlow = MutableStateFlow(InventoryViewState())
    val uiStateFlow: StateFlow<InventoryViewState> = _uiStateFlow

    private val shopData = gameRepository.shopDataFlow
    private val inventoryData = gameRepository.inventoryDataFlow

    init {
        viewModelScope.launch {
            gameRepository.updateShop()
            _uiStateFlow.value = _uiStateFlow.value.copy(shopData = shopData)
            _uiStateFlow.value = _uiStateFlow.value.copy(inventoryData = inventoryData)
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

    fun useItem(itemToBuy: ShopData, it: String) {
        viewModelScope.launch {
            gameRepository.useItem(itemToBuy, it)
        }
    }

    fun updateBitcoinBalance(cost: Long){
        viewModelScope.launch {
            Log.d("BitcoinUpdate", "Updating balance by $cost")
            gameRepository.issueBitcoins(cost)
        }
    }
}