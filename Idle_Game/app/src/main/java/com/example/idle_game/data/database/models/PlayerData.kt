package com.example.idle_game.data.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity
data class PlayerData (
    @PrimaryKey(autoGenerate = false)
    val uid: Int = 1,
    @Json(name = "username") val username: String,
    @Json(name = "password") val password: String,
    @Json(name = "refresh_token") val refreshToken: String,
    @Json(name = "access_token") val accessToken: String?,
)