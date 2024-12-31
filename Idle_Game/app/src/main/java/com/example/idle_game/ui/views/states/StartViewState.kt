package com.example.idle_game.ui.views.states

data class StartViewState(
    val coins: Long = 0,
    val hackers: Int = 0,
    val bots: Int = 0,
    val miners: Int = 0,
    val coinsPerSec: Long = 0,
    val activeBoost: Int = 0
)