package com.example.idle_game.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.idle_game.data.database.models.PlayerData

@Database(entities = [PlayerData::class, ], version = 1, exportSchema = false)
abstract class GameDatabase : RoomDatabase() {
    abstract val gameDao: GameDao
}