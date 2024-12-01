package com.example.idle_game.ui.views.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


//@HiltViewModel
//class LoginViewModel @Inject constructor(
//    private val gameRepository: GameRepository,
class LoginViewModel(
    onLoginSuccess: () -> Unit
) : ViewModel() {
    init {
        viewModelScope.launch {
            checkSignIn { onLoginSuccess() }
        }
    }

    fun buttonSubmit(name: String) {
        viewModelScope.launch {
            //gameRepository.createNewInventory()  // Ensure the inventory is created first
            //gameRepository.addBoost()  // Add boost after inventory is created
            //gameRepository.activateBoost()
            //gameRepository.signUp(name, "123")
            //gameRepository.login()
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
//          var isSup = gameRepository.isAlreadySignedUp()
//          if(isSup)
//            onLoginSuccess()
        }
    }
}
