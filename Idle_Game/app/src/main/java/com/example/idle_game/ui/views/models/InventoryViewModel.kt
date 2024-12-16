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

    fun useItem(item: ShopData){

    }
}