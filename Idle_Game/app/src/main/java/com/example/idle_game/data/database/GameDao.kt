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

    @Query("UPDATE inventorydata SET bitcoins = :bitcoins")
    suspend fun updateBitcoins(bitcoins: Int)

    @Query("UPDATE inventorydata SET activeHackers = :active, unusedHackers = :unused")
    suspend fun updateHackers(active: Int, unused: Int)

    @Query("UPDATE inventorydata SET unusedHackers = :unusedHackers")
    suspend fun updateUnusedHackers(unusedHackers: Int)

    @Query("UPDATE inventorydata SET activeCryptoMiners = :active, unusedCryptoMiners = :unused")
    suspend fun updateCryptoMiners(active: Int, unused: Int)

    @Query("UPDATE inventorydata SET unusedHackers = :unusedCryptoMiners")
    suspend fun updateUnusedCryptoMiners(unusedCryptoMiners: Int)

    @Query("UPDATE inventorydata SET activeBotnets = :active, unusedBotnets = :unused")
    suspend fun updateBotnets(active: Int, unused: Int)

    @Query("UPDATE inventorydata SET unusedBotnets = :unusedBotnets")
    suspend fun updateUnusedBotnets(unusedBotnets: Int)

    @Query("SELECT * FROM scoreboarddata")
    fun getScoreBoard(): Flow<ScoreBoardData>

}