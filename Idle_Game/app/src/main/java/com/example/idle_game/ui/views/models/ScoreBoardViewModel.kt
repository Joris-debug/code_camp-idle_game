package com.example.idle_game.ui.views.models;

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.idle_game.data.repositories.GameRepository
import com.example.idle_game.ui.views.states.ScoreBoardViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScoreBoardViewModel @Inject constructor(
    private val gameRepository: GameRepository,
) : ViewModel() {

    private val _uiStateFlow = MutableStateFlow(ScoreBoardViewState())
    val uiStateFlow: StateFlow<ScoreBoardViewState> = _uiStateFlow

    private val scoreData = gameRepository.scoreBoardDataFlow
    private var isButtonEnabled = true

    init {
        viewModelScope.launch {
            gameRepository.signIn()
            gameRepository.login()
            gameRepository.updateBitcoins(-100)
        }
        viewModelScope.launch {
            gameRepository.updateScoreBoard()
            gameRepository.fetchScoreBoard()
            _uiStateFlow.value = _uiStateFlow.value.copy(scoreData = scoreData)
        }
    }

    fun refreshScoreBoard() {
        if (!isButtonEnabled) {
            return
        }
        isButtonEnabled = false
        viewModelScope.launch {
            gameRepository.updateScoreBoard()
            gameRepository.fetchScoreBoard()
            isButtonEnabled = true
        }
    }

}