package com.example.idle_game.ui.views.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.idle_game.data.repositories.GameRepository
import com.example.idle_game.data.repositories.SettingsRepository
import com.example.idle_game.ui.views.states.SettingsViewState
import com.example.idle_game.util.OPTION_NOTIFICATIONS
import com.example.idle_game.util.OPTION_THEME
import com.example.idle_game.worker.NotificationWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    val settingsRepository: SettingsRepository,
    val gameRepository: GameRepository,
    private val workManager: WorkManager
) : ViewModel() {
    private val _viewState = MutableStateFlow(SettingsViewState())
    val viewState: StateFlow<SettingsViewState> = _viewState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.getOptions(3).collect { options ->
                _viewState.value = _viewState.value.copy(
                    switchState = options,
                    username = gameRepository.getPlayerDataFlow().first().username,
                    contrast = settingsRepository.getContrast().first()
                )
            }
        }
    }

    /**
     * handles value changes in the view
     */
    fun saveOption(option: Boolean, num: Int) {
        viewModelScope.launch {
            settingsRepository.saveOption(option, num)
            when (num) {
                OPTION_NOTIFICATIONS -> notification(option)
                OPTION_THEME -> selectTheme(option)
            }
        }
    }

    fun saveContrast(contrast: Int) {
        viewModelScope.launch {
            settingsRepository.saveContrast(contrast)
            _viewState.value = _viewState.value.copy(
                contrast = contrast
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            gameRepository.logout()
        }
    }

    fun selectTheme(theme: Boolean) {
        viewModelScope.launch {
            settingsRepository.saveTheme(theme)
        }
    }

    fun notification(value: Boolean) {
        workManager.cancelAllWork()
        if (value) {
            val periodicWorkRequest =
                PeriodicWorkRequest.Builder(NotificationWorker::class.java, 15, TimeUnit.MINUTES)
                    .build()
            workManager.enqueue(periodicWorkRequest)
        }
    }

    /**
     * Submit CheatCode with new-line
     */
    fun checkCheatCode(text: String): String {
        if (text.isEmpty()) {
            return ""
        }
        if (text.last() == '\n') {
            runCheatCode(text)
            return ""
        }
        return text
    }

    /**
     * Cheat codes:
     *  add btc {number}
     *  set btc {number}
     */
    private fun runCheatCode(input: String) {
        var text = input.removeSuffix("\n")
        viewModelScope.launch {
            if (text.startsWith("add btc ")) {
                text = text.removePrefix("add btc ")
                try {
                    gameRepository.addBitcoins(text.toLong())
                } catch (_: Exception) {
                }
            } else if (text.startsWith("set btc ")) {
                text = text.removePrefix("set btc ")
                try {
                    gameRepository.setBitcoin(text.toLong())
                } catch (_: Exception) {
                }
            }
        }
    }
}

