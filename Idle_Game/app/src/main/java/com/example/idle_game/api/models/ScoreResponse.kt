package com.example.idle_game.api.models

import com.example.idle_game.data.database.models.ScoreBoardData
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ScoreResponse(
    @Json(name = "username") val username: String,
    @Json(name = "score") val score: Long
) {

    companion object {
        const val MAX_USERNAME_SIZE = 20
    }

    fun toScoreBoardData(): ScoreBoardData {
        return ScoreBoardData(
            username = formatName(username),
            score = score
        )
    }

    private fun formatName(input: String): String {
        val formattedString = input.replace("\n", " ")
        return if (formattedString.length > MAX_USERNAME_SIZE) {
            formattedString.substring(0, MAX_USERNAME_SIZE) + "..."
        } else {
            formattedString
        }
    }

}