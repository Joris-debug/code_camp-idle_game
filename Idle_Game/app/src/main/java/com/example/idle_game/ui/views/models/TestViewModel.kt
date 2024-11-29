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
            gameRepository.signUp("45fgd3w4", "123")
        }
    }
}