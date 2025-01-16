package com.example.idle_game.ui.views.states

import com.example.idle_game.data.database.models.InventoryData

data class StartViewState(
    val coins: String = "0",
    val coinsPerSec: String = "0",
    val activeBoost: Int = 0,
    val hackerCount: String = "0",
    val minerCount: String = "0",
    val botnetCount: String = "0",
    )