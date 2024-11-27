package com.example.idle_game.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.idle_game.data.database.models.PlayerData
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    @Query("SELECT * FROM playerdata")
    fun getGame(): Flow<PlayerData>

    @Upsert
    suspend fun insert(game: PlayerData)

}