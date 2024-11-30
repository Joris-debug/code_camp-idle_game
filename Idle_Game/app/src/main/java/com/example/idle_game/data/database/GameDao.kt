package com.example.idle_game.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.idle_game.data.database.models.InventoryData
import com.example.idle_game.data.database.models.PlayerData
import com.example.idle_game.data.database.models.ScoreBoardData
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    @Query("SELECT * FROM playerdata")
    fun getPlayer(): Flow<PlayerData>

    @Upsert
    suspend fun insertPlayer(player: PlayerData)

    @Query("SELECT * FROM inventorydata")
    fun getInventory(): Flow<InventoryData>

    @Upsert
    suspend fun insertInventory(inventory: InventoryData)

    @Query("SELECT * FROM scoreboarddata")
    fun getScoreBoard(): Flow<ScoreBoardData>

}