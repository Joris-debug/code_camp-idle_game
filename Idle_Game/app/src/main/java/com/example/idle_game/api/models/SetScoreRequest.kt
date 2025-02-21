package com.example.idle_game.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SetScoreRequest(
    @Json(name = "username") val username: String,
    @Json(name = "score") val score: Long
)
