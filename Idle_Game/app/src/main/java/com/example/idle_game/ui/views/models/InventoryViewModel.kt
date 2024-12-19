package com.example.idle_game.ui.views.models
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val gameRepository: GameRepository
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

     fun buyItem(item: ShopData) {
         viewModelScope.launch {
             when (item.name) {
                 "low Boost" -> gameRepository.addLowBoost()
                 "medium Boost" -> gameRepository.addMediumBoost()
                 "high Boost" -> gameRepository.addHighBoost()
                 "low passive" -> gameRepository.addNewHacker()
                 "medium passive" -> gameRepository.addNewCryptoMiner()
                 "high passive" -> gameRepository.addNewBotnet()
                 "upgrade lvl 2" -> gameRepository.addUpgradeLvl2()
                 "upgrade lvl 3" -> gameRepository.addUpgradeLvl3()
                 "upgrade lvl 4" -> gameRepository.addUpgradeLvl4()
                 "upgrade lvl 5" -> gameRepository.addUpgradeLvl5()
             }
         }
    }

    fun useItem(item: ShopData, useOn: String){
        viewModelScope.launch {
            if (!gameRepository.isBoostActive()){
                when(item.name){
                    "low Boost" -> gameRepository.activateLowBoost()
                    "medium Boost" -> gameRepository.activateMediumBoost()
                    "high Boost" -> gameRepository.activateHighBoost()
                }
            }
            when(item.name){
                "upgrade lvl 2" -> {
                    when (useOn){
                        "Hacker" -> gameRepository.upgradeHacker(1)
                        "Miner" -> gameRepository.upgradeCryptoMiner(1)
                        "BotNet" -> gameRepository.upgradeBotnet(1)
                    }
                }
                "upgrade lvl 3" -> {
                    when (useOn){
                        "Hacker" -> gameRepository.upgradeHacker(2)
                        "Miner" -> gameRepository.upgradeCryptoMiner(2)
                        "BotNet" -> gameRepository.upgradeBotnet(2)
                    }
                }
                "upgrade lvl 4" -> {
                    when (useOn){
                        "Hacker" -> gameRepository.upgradeHacker(3)
                        "Miner" -> gameRepository.upgradeCryptoMiner(3)
                        "BotNet" -> gameRepository.upgradeBotnet(3)
                    }
                }
                "upgrade lvl 5" -> {
                    when (useOn){
                        "Hacker" -> gameRepository.upgradeHacker(4)
                        "Miner" -> gameRepository.upgradeCryptoMiner(4)
                        "BotNet" -> gameRepository.upgradeBotnet(4)
                    }
                }
            }
        }
    }
}