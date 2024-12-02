package com.example.idle_game.ui.views.models

import android.util.Log
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
            gameRepository.inventoryDataFlow.collect { inventoryData ->
                Log.d("TestViewModel", "Boost count: ${inventoryData.lowBoosts}")  // Log the boosts
                Log.d("TestViewModel", "Active until: ${inventoryData.boostActiveUntil}")
            }

            gameRepository.signUp("uziu1412313we867890sfvghnhg", "123")
            gameRepository.login()

        }

    }
}