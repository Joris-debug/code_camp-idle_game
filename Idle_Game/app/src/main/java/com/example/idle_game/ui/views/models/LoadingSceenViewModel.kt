package com.example.idle_game.ui.views.models

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.idle_game.data.repositories.GameRepository
import com.example.idle_game.ui.views.states.LoadingScreenViewState
import com.example.idle_game.util.isInternetAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoadingSceenViewModel @Inject constructor(
    private val gameRepository: GameRepository,
) : ViewModel() {
    private val _viewState = MutableStateFlow(LoadingScreenViewState())
    val viewState: StateFlow<LoadingScreenViewState> get() = _viewState

    fun init(onLoginSuccess: () -> Unit, onLoginFailure: () -> Unit, context: Context, onWifiOK: () -> Unit) {
        var wifiOk = false
        viewModelScope.launch {
            while (!wifiOk) {
                if (!isInternetAvailable(context)) {
                    _viewState.value = _viewState.value.copy(
                        connectionString = "Bitte überprüfe deine Internetverbindung"
                    )
                    wifiOk = false
                    delay(1000)
                } else {
                    wifiOk = true
                    _viewState.value = _viewState.value.copy(
                        connectionString = ""
                    )
                    onWifiOK()
                    var boolLoginFailed = false
                    Log.d("DEBUG", "Vor Login")
                    gameRepository.login { boolLoginFailed = true }
                    Log.d("DEBUG", "Nach Login: $boolLoginFailed")
                    if(boolLoginFailed) {
                        onLoginFailure()

                    } else {
                        onLoginSuccess()
                    }
                }
            }

        }
    }
}
