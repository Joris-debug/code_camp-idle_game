package com.example.idle_game.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserCredentialsRequest(
    @Json(name = "username") val username: String,
    @Json(name = "password") val password: String
)
