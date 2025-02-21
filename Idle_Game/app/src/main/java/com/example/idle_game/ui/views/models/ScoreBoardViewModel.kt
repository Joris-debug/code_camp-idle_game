package com.example.idle_game.ui.views.models;

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.idle_game.data.repositories.GameRepository
import com.example.idle_game.data.repositories.SettingsRepository
import com.example.idle_game.ui.views.states.ScoreBoardViewState
import com.example.idle_game.util.OPTION_SORTNUMBERS
import com.example.idle_game.util.SoundManager
import com.example.idle_game.util.shortBigNumbers
import com.example.idle_game.worker.ScoreBoardWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ScoreBoardViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val workManager: WorkManager,
    val soundManager: SoundManager,
    val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _uiStateFlow = MutableStateFlow(ScoreBoardViewState())
    val uiStateFlow: StateFlow<ScoreBoardViewState> = _uiStateFlow

    private var showShorted: Boolean = false
    private val scoreData = gameRepository.getScoreBoardDataFlow()
    private val playerData = gameRepository.getPlayerDataFlow()
    private var isButtonEnabled = true

    init {
        viewModelScope.launch {
            showShorted = settingsRepository.getOption(OPTION_SORTNUMBERS).first()
            gameRepository.updateScoreBoard()
            gameRepository.fetchScoreBoard()
            _uiStateFlow.value = _uiStateFlow.value.copy(scoreData = scoreData)
            _uiStateFlow.value = _uiStateFlow.value.copy(playerData = playerData)
            startWork()
        }
    }

    fun toDisplay(value: Number): String {
        return if (showShorted) {
            shortBigNumbers(value.toLong())
        } else {
            value.toString()
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

    private fun startWork() {
        val scoreWorkRequest =
            PeriodicWorkRequestBuilder<ScoreBoardWorker>(15, TimeUnit.MINUTES).build()
        workManager.enqueueUniquePeriodicWork(
            WORK_KEY, ExistingPeriodicWorkPolicy.UPDATE, scoreWorkRequest
        )
    }

    companion object {
        const val WORK_KEY = "score-updater"
    }
}