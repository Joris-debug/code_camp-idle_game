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

    init {
        viewModelScope.launch {
            gameRepository.updateShop()
            _uiStateFlow.value = _uiStateFlow.value.copy(shopData = shopData)
        }



    }


}