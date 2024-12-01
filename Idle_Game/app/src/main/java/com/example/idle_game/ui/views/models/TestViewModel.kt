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
            gameRepository.addBoost()  // Add boost after inventory is created
            gameRepository.activateBoost()
            // Collect data from the flow
            /*
            gameRepository.inventoryDataFlow.collect { inventoryData ->
                Log.d("TestViewModel", "Boost count: ${inventoryData.boosts}")  // Log the boosts
                Log.d("TestViewModel", "Active until: ${inventoryData.boostActiveUntil}")
            }*/

            gameRepository.signUp("uziunhg", "123")
            gameRepository.login()

        }

    }
}