package com.example.idle_game.api.models

import com.example.idle_game.data.database.models.ScoreBoardData
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ScoreResponse(
    @Json(name = "username") val username: String,
    @Json(name = "score") val score: Long
) {
    fun toScoreBoardData(): ScoreBoardData {
        return ScoreBoardData(
            username = username,
            score = score
        )
    }
}