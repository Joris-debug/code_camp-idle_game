package com.example.idle_game.ui.views.states

import com.example.idle_game.data.database.models.InventoryData

data class StartViewState(
    val coins: Long = 0,
    val coinsPerSec: Long = 0,
    val activeBoost: Int = 0,
    val hackerLvl1: Int = 0,
    val hackerLvl2: Int = 0,
    val hackerLvl3: Int = 0,
    val hackerLvl4: Int = 0,
    val hackerLvl5: Int = 0,
    val minerLvl1: Int = 0,
    val minerLvl2: Int = 0,
    val minerLvl3: Int = 0,
    val minerLvl4: Int = 0,
    val minerLvl5: Int = 0,
    val botnetLvl1: Int = 0,
    val botnetLvl2: Int = 0,
    val botnetLvl3: Int = 0,
    val botnetLvl4: Int = 0,
    val botnetLvl5: Int = 0
    )