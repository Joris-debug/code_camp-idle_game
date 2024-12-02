package com.example.idle_game.ui.views.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.idle_game.data.repositories.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TestViewModel @Inject constructor(
    private val gameRepository: GameRepository,
): ViewModel() {

    init {
        viewModelScope.launch {
            gameRepository.createNewInventory()  // Ensure the inventory is created first
            gameRepository.addLowBoost()  // Add boost after inventory is created
            gameRepository.activateLowBoost()
            // Collect data from the flow

            gameRepository.signUp("unusedName6", "123")
            gameRepository.login()
            gameRepository.updateShop()

            gameRepository.shopDataFlow.collect { itemData ->
                itemData.forEach { item ->
                    println("Collected item: $item")
                }
            }
        }

    }
}