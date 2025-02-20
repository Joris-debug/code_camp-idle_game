package com.example.idle_game.ui.views.states

import com.example.idle_game.ui.theme.LOW_CONTRAST

data class SettingsViewState(
    val switchState: List<Boolean> = List(3) { true },
    val username: String = "",
    val contrast: Int = LOW_CONTRAST
)