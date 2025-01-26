package com.example.idle_game.ui.views.states

import com.example.idle_game.data.database.models.InventoryData
import com.example.idle_game.data.database.models.ShopData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class InventoryViewState(
    val shopData: Flow<List<ShopData>> = emptyFlow(),
    val inventoryData: Flow<InventoryData> = emptyFlow(),
    val selectedItem: ShopData? = null,
    val activeBoost: Int = 0,
    val amountUpgradeLvl2: Int = 0,
    val amountUpgradeLvl3: Int = 0,
    val amountUpgradeLvl4: Int = 0,
    val amountUpgradeLvl5: Int = 0,

    val amountHackerLvl1: Int = 0,
    val amountHackerLvl2: Int = 0,
    val amountHackerLvl3: Int = 0,
    val amountHackerLvl4: Int = 0,

    val amountMinerLvl1: Int = 0,
    val amountMinerLvl2: Int = 0,
    val amountMinerLvl3: Int = 0,
    val amountMinerLvl4: Int = 0,

    val amountBotNetLvl1: Int = 0,
    val amountBotNetLvl2: Int = 0,
    val amountBotNetLvl3: Int = 0,
    val amountBotNetLvl4: Int = 0
    )