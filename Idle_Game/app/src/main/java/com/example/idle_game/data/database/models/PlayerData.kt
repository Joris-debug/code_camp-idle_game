package com.example.idle_game.data.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity
data class PlayerData (
    @PrimaryKey(autoGenerate = false)
    val uid: Int = 1,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "refresh_token") val refreshToken: String,
    @ColumnInfo(name = "access_token") val accessToken: String?,
)