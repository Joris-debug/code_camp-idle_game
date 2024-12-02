package com.example.idle_game.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ServerResponse(
    @Json(name = "error") val error: String? = null,
    @Json(name = "message") val message: String? = null,
)