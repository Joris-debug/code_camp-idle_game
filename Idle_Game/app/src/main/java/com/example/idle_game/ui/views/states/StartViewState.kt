package com.example.idle_game.ui.views.states

data class StartViewState(
    val coins: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val hackers: Int = 0,
    val bots: Int = 0,
    val miners: Int = 0,
    val coinsPerSec: Int = 0
)