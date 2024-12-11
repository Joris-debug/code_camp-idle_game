package com.example.idle_game.ui.views.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.idle_game.data.repositories.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val gameRepository: GameRepository,
) : ViewModel() {

    companion object {
        const val PASSWORD_LENGTH = 10
        const val PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    }

    fun init(onLoginSuccess: () -> Unit) {
        viewModelScope.launch {
            checkSignIn { onLoginSuccess() }
        }
    }

    private fun generateRandomString(
        length: Int = PASSWORD_LENGTH,
        allowedChars: String = PASSWORD_CHARS
    ): String {
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    fun buttonSubmit(name: String, onLoginSuccess: () -> Unit) {
        viewModelScope.launch {
            gameRepository.createNewInventory()  // Ensure the inventory is created first
            var success = true;
            gameRepository.signUp(name, generateRandomString(10), {success = false})
            gameRepository.login()
            if(success) {
                onLoginSuccess()
            }
        }
    }

    fun checkSignIn(onLoginSuccess: () -> Unit) {
        viewModelScope.launch {
            /* AUFRUF in init {}
        * if signdin
        *   login -> worker??
        *   isLoggedIn = true (MainAktivity)
        *
        *
        * */
            var isSup = true
            gameRepository.login({isSup = false})
            if (isSup) {
                onLoginSuccess()
            }
        }
    }
}