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

    @Query("SELECT * FROM playerdata WHERE uid = 1")
    fun getPlayer(): Flow<PlayerData>

    @Upsert
    suspend fun insertPlayer(player: PlayerData)

    @Query("UPDATE playerdata SET access_token = :accessToken")
    suspend fun updateAccessToken(accessToken: String)

    @Query("SELECT * FROM inventorydata WHERE uid = 1")
    fun getInventory(): Flow<InventoryData>

    @Upsert
    suspend fun insertInventory(inventory: InventoryData)

    @Query("UPDATE inventorydata SET bitcoins = :bitcoins")
    suspend fun updateBitcoins(bitcoins: Int)

    @Query("UPDATE inventorydata SET active_hackers = :active, unused_hackers = :unused")
    suspend fun updateHackers(active: Int, unused: Int)

    @Query("UPDATE inventorydata SET unused_hackers = :unusedHackers")
    suspend fun updateUnusedHackers(unusedHackers: Int)

    @Query("UPDATE inventorydata SET active_crypto_miners = :active, unused_crypto_miners = :unused")
    suspend fun updateCryptoMiners(active: Int, unused: Int)

    @Query("UPDATE inventorydata SET unused_crypto_miners = :unusedCryptoMiners")
    suspend fun updateUnusedCryptoMiners(unusedCryptoMiners: Int)

    @Query("UPDATE inventorydata SET active_botnets = :active, unused_botnets = :unused")
    suspend fun updateBotnets(active: Int, unused: Int)

    @Query("UPDATE inventorydata SET unused_botnets = :unusedBotnets")
    suspend fun updateUnusedBotnets(unusedBotnets: Int)

    @Query("UPDATE inventorydata SET low_boosts = :boosts")
    suspend fun updateLowBoosts(boosts: Int)

    @Query("UPDATE inventorydata SET medium_boosts = :boosts")
    suspend fun updateMediumBoosts(boosts: Int)

    @Query("UPDATE inventorydata SET high_boosts = :boosts")
    suspend fun updateHighBoosts(boosts: Int)

    @Query("UPDATE inventorydata SET active_boost_type = :type, boost_active_until = :until")
    suspend fun updateBoostActivation(type: Int, until: Long)

    @Query("SELECT * FROM scoreboarddata")
    fun getScoreBoard(): Flow<ScoreBoardData>

}