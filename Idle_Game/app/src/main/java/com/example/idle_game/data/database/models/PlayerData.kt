package com.example.idle_game.data.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PlayerData (
    @PrimaryKey(autoGenerate = false)
    val uid: Int = 1,
)