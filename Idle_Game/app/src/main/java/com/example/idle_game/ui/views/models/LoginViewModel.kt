package com.example.idle_game.ui.views.models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.idle_game.data.repositories.GameRepository
import com.example.idle_game.ui.views.states.LoginViewState
import com.example.idle_game.ui.views.states.StartViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {
    private val _viewState = MutableStateFlow(LoginViewState())
    val viewState: StateFlow<LoginViewState> get() = _viewState

    fun checkInput(input: String, username: Boolean): String {
        val allowedCharsName: String =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val allowedCharsPassword: String =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789.,+-_/&!?"
        var errorString = ""
        var correctedString = input

        if (input.length > 20) {
            correctedString = input.substring(0, 20)
            errorString = "Maximal 20 Zeichen!"
        }

        if (username) {
            if (input.any { it !in allowedCharsName }) {
                if (errorString.isNotEmpty()) {
                    errorString += "\n"
                }
                errorString += "Gültige Zeichen im Benutzernamen sind: a-z, A-Z, 0-9"
            }
            if (errorString.isNotEmpty()) {
                _viewState.value =
                    _viewState.value.copy(errorMessage = errorString)
            }
            correctedString = correctedString.filter { it in allowedCharsName }
            return correctedString
        }
        if (input.any { it !in allowedCharsPassword }) {
            if (errorString.isNotEmpty()) {
                errorString += "\n"
            }
            errorString += "Gültige Zeichen im Passwort sind: a-z, A-Z, 0-9, .,+-_/&!?"
        }
        if (errorString.isNotEmpty()) {
            _viewState.value =
                _viewState.value.copy(errorMessage = errorString)
        }
        correctedString = correctedString.filter { it in allowedCharsPassword }
        return correctedString
    }

    fun buttonSubmit(username: String, password: String, onLoginSuccess: () -> Unit) {
        viewModelScope.launch {
            if (username.isEmpty()) {
                _viewState.value =
                    _viewState.value.copy(errorMessage = "Username darf nicht leer sein!")
            } else {
                var success = true
                gameRepository.signIn(username, password, { success = false })
                if (!success) {
                    gameRepository.signUp(
                        username,
                        password,
                        {
                            success = false; _viewState.value =
                            _viewState.value.copy(errorMessage = "Benutzername bereits vergeben oder Passwort ungültig")
                        })
                }

                gameRepository.login({ success = false })
                if (success) {
                    onLoginSuccess()
                }
            }
        }
    }
}
