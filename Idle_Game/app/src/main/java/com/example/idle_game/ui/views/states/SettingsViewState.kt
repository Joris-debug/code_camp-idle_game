package com.example.idle_game.ui.views.states

data class SettingsViewState(
    val switchState: List<Boolean> = List(3) { true },
    val username: String = ""
)