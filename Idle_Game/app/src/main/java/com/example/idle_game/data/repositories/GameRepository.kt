package com.example.idle_game.data.repositories

import android.content.SharedPreferences
import com.example.idle_game.api.GameApi
import com.example.idle_game.api.models.SignUpRequest
import com.example.idle_game.api.models.SignUpResponse
import com.example.idle_game.data.database.GameDao
import com.example.idle_game.data.database.models.PlayerData

class GameRepository(
    private val api: GameApi,
    private val gameDao: GameDao,
    private val sharedPreferences: SharedPreferences
) {
    val playerDataFlow = gameDao.getPlayer()

    suspend fun signUp(username: String, password: String) {
        val signUpRequest = SignUpRequest(username = username, password = password)
        val resp = api.signUp(signUpRequest)

        if (resp.error == null && resp.message != null) {
            val refreshToken = sharedPreferences.getString("refresh_token", null)
            if (refreshToken != null) {
                val playerData = PlayerData(
                    username = username,
                    password = password,
                    refreshToken = refreshToken,
                    accessToken = null
                )
                gameDao.insertPlayer(playerData)
            }
        }
    }
}
