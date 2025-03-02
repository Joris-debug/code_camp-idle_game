package com.example.idle_game.ui.views.models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.idle_game.data.repositories.GameRepository
import com.example.idle_game.ui.views.states.LoginViewState
import com.example.idle_game.util.SoundManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    val soundManager: SoundManager
) : ViewModel() {
    private val _viewState = MutableStateFlow(LoginViewState())
    val viewState: StateFlow<LoginViewState> get() = _viewState

    fun checkInput(input: String, username: Boolean): String {
        var errorString = ""
        var correctedString = input

        if (input.length > MAX_INPUT_LENGTH) {
            correctedString = input.substring(0, MAX_INPUT_LENGTH)
            errorString = "Maximal $MAX_INPUT_LENGTH Zeichen!"
        } else if (input.length < MAX_INPUT_LENGTH) {
            if (_viewState.value.errorMessage.startsWith("Maximal $MAX_INPUT_LENGTH Zeichen!")) {
                _viewState.value =
                    _viewState.value.copy(errorMessage = "")
            }
        }

        if (username) {
            if (input.any { it !in ALLOWED_CHARS_NAME }) {
                if (errorString.isNotEmpty()) {
                    errorString += "\n"
                }
                errorString += "Gültige Zeichen im Benutzernamen sind: a-z, A-Z, 0-9"
            }
            if (errorString.isNotEmpty()) {
                _viewState.value =
                    _viewState.value.copy(errorMessage = errorString)
            }
            correctedString = correctedString.filter { it in ALLOWED_CHARS_NAME }
            return correctedString
        }
        if (input.any { it !in ALLOWED_CHARS_PASSWORD }) {
            if (errorString.isNotEmpty()) {
                errorString += "\n"
            }
            errorString += "Gültige Zeichen im Passwort sind: a-z, A-Z, 0-9, .,+-_/&!?"
        }
        if (errorString.isNotEmpty()) {
            _viewState.value =
                _viewState.value.copy(errorMessage = errorString)
        }
        correctedString = correctedString.filter { it in ALLOWED_CHARS_PASSWORD }
        return correctedString
    }

    fun buttonSubmit(username: String, password: String, onLoginSuccess: () -> Unit) {
        viewModelScope.launch {
            if (username.isEmpty()) {
                _viewState.value =
                    _viewState.value.copy(errorMessage = "Username darf nicht leer sein!")
            } else {
                var success = gameRepository.signIn(username, password)
                Log.e("Success", "true")
                if (!success) {
                    success = gameRepository.signUp(
                        username,
                        password,
                        {
                            _viewState.value =
                                _viewState.value.copy(
                                    errorMessage = "Benutzername bereits vergeben oder Passwort ungültig"
                                )
                        }
                    )
                }

                if (success) {
                    do {
                        delay(100)
                        success = gameRepository.login()
                        if (success) {
                            onLoginSuccess()
                        }
                    } while (!success)
                }
            }
        }
    }

    companion object {
        const val ALLOWED_CHARS_NAME =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        const val ALLOWED_CHARS_PASSWORD =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789.,+-_/&!?"
        const val MAX_INPUT_LENGTH = 20
    }
}
