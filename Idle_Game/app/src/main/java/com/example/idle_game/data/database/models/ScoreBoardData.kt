package com.example.idle_game.data.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ScoreBoardData (
    @PrimaryKey(autoGenerate = false) val id: String,
    @ColumnInfo(name = "score") val score: Int,
    @ColumnInfo(name = "username") val username: String,
)