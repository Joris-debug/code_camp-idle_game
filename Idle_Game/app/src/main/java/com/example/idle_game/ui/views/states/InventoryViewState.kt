package com.example.idle_game.ui.views.states

import com.example.idle_game.data.database.models.InventoryData
import com.example.idle_game.data.database.models.ShopData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class InventoryViewState(
    val shopData: Flow<List<ShopData>> = emptyFlow(),
    val inventoryData: InventoryData? = null
    )