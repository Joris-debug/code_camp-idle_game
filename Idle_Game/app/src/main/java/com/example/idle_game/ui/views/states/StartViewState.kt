package com.example.idle_game.ui.views.states

data class StartViewState(
    val counter: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)