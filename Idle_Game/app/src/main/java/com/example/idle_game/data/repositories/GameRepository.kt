package com.example.idle_game.data.repositories

import android.content.SharedPreferences
import com.example.idle_game.api.GameApi
import com.example.idle_game.api.models.SignUpRequest
import com.example.idle_game.api.models.SignUpResponse
import com.example.idle_game.data.database.GameDao
import com.example.idle_game.data.database.models.PlayerData
import retrofit2.HttpException

class GameRepository(
    private val api: GameApi,
    private val gameDao: GameDao,
    private val sharedPreferences: SharedPreferences
) {
    val playerDataFlow = gameDao.getPlayer()
    val inventoryDataFlow = gameDao.getInventory()

    suspend fun signUp(username: String, password: String) {
        val signUpRequest = SignUpRequest(username = username, password = password)
        try {
            val resp = api.signUp(signUpRequest)
            val refreshToken = sharedPreferences.getString("refresh_token", null)
            if(refreshToken != null) {
                val playerData = PlayerData(
                    username = username,
                    password = password,
                    refreshToken = refreshToken,
                    accessToken = null
                )
                gameDao.insertPlayer(playerData)
            }
        } catch (e: HttpException) {
            println(e);
        }
    }

    suspend fun updateBitcoins(bitcoins: Int) {
        gameDao.updateBitcoins(bitcoins)
    }

    // Adds a new hacker to the inventory
    suspend fun addUnusedHacker() {
        inventoryDataFlow.collect { inventoryData ->
            var unusedHackers = inventoryData.unusedHackers
            gameDao.updateUnusedHackers(++unusedHackers);
        }
    }

    // Activates a single unused hacker
    suspend fun activateHacker() {
        inventoryDataFlow.collect { inventoryData ->
            var activeHackers = inventoryData.activeHackers
            var unusedHackers = inventoryData.unusedHackers
            if(unusedHackers > 0) {
                gameDao.updateHackers(++activeHackers, --unusedHackers);
            }
        }
    }

    // Adds a new crypto miner to the inventory
    suspend fun addUnusedCryptoMiner() {
        inventoryDataFlow.collect { inventoryData ->
            var unusedCryptoMiners = inventoryData.unusedCryptoMiners
            gameDao.updateUnusedHackers(++unusedCryptoMiners);
        }
    }

    // Activates a single unused crypto miner
    suspend fun activateCryptoMiner() {
        inventoryDataFlow.collect { inventoryData ->
            var activeCryptoMiners = inventoryData.activeCryptoMiners
            var unusedCryptoMiners = inventoryData.unusedCryptoMiners
            if(unusedCryptoMiners > 0) {
                gameDao.updateHackers(++activeCryptoMiners, --unusedCryptoMiners);
            }
        }
    }

    // Adds a new botnet to the inventory
    suspend fun addUnusedBotnet() {
        inventoryDataFlow.collect { inventoryData ->
            var unusedBotnets = inventoryData.unusedBotnets
            gameDao.updateUnusedHackers(++unusedBotnets);
        }
    }

    // Activates a single unused botnet
    suspend fun activateBotnet() {
        inventoryDataFlow.collect { inventoryData ->
            var activeBotnets = inventoryData.activeBotnets
            var unusedBotnets = inventoryData.unusedBotnets
            if(unusedBotnets > 0) {
                gameDao.updateHackers(++activeBotnets, --unusedBotnets);
            }
        }
    }

}
