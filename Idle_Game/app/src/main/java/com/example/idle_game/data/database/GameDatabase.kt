package com.example.idle_game.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.idle_game.data.database.models.InventoryData
import com.example.idle_game.data.database.models.PlayerData
import com.example.idle_game.data.database.models.ScoreBoardData
import com.example.idle_game.data.database.models.ShopData

@Database(
    entities = [PlayerData::class, ScoreBoardData::class, InventoryData::class, ShopData::class],
    version = 2,
    exportSchema = true
)
abstract class GameDatabase : RoomDatabase() {
    abstract val gameDao: GameDao
}