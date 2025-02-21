package com.example.idle_game.data.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ScoreBoardData(
    @PrimaryKey val username: String,
    @ColumnInfo(name = "score") val score: Long
)