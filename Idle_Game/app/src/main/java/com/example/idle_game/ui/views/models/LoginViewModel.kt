package com.example.idle_game.ui.views.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.idle_game.data.repositories.GameRepository
import com.example.idle_game.ui.views.states.LoginViewState
import com.example.idle_game.ui.views.states.StartViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {
    private val _viewState = MutableStateFlow(LoginViewState())
    val viewState: StateFlow<LoginViewState> get() = _viewState

    public fun checkInput(input: String, username: Boolean): String {
        val allowedCharsName: String =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val allowedCharsPassword: String =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789.,+-_/&!?"


        if (username) {
            if (input.any { it !in allowedCharsName }) {
                _viewState.value =
                    _viewState.value.copy(errorMessage = "Gültige Zeichen im Benutzernamen sind: a-z, A-Z, 0-9")
            }

            return input.filter { it in allowedCharsName }
        }
        if (input.any { it !in allowedCharsPassword }) {
            _viewState.value =
                _viewState.value.copy(errorMessage = "Gültige Zeichen im Passwort sind: a-z, A-Z, 0-9, .,+-_/&!?")
        }
        return input.filter { it in allowedCharsPassword }
    }

    fun buttonSubmit(username: String, password: String, onLoginSuccess: () -> Unit) {
        viewModelScope.launch {
            gameRepository.createNewInventory()  // Ensure the inventory is created first
            var success = true;
            gameRepository.signIn(username, password, {success = false})
            if(!success) {
                gameRepository.signUp(
                    username,
                    password,
                    {
                        success = false; _viewState.value =
                        _viewState.value.copy(errorMessage = "Benutzername bereits vergeben oder Passwort ungültig")
                    })
            }

            gameRepository.login({success = false})
            if (success) {
                onLoginSuccess()
            }
        }
    }
}
