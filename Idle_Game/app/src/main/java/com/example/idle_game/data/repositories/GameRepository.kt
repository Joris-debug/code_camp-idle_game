package com.example.idle_game.data.repositories

import android.content.SharedPreferences
import com.example.idle_game.api.GameApi
import com.example.idle_game.api.models.SignUpRequest
import com.example.idle_game.data.database.GameDao
import com.example.idle_game.data.database.models.InventoryData
import com.example.idle_game.data.database.models.PlayerData
import kotlinx.coroutines.flow.first
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
            //TODO add better error handling
        }
    }

    suspend fun login() {
        val playerData = playerDataFlow.first()
        try {
            val resp = api.login("refresh_token="+playerData.refreshToken)
            println(resp)
        } catch (e: HttpException) {
            println(e);
            //TODO add better error handling
        }
    }

    // Call this function before accessing the inventory for the first time
    suspend fun createNewInventory() {
        gameDao.insertInventory(InventoryData())
    }

    // TODO add error handing if no inventory exists (all functions)
    suspend fun updateBitcoins(bitcoins: Int) {
        gameDao.updateBitcoins(bitcoins)
    }

    // Adds a new hacker to the inventory
    suspend fun addUnusedHacker() {
        val inventoryData = inventoryDataFlow.first()
        var unusedHackers = inventoryData.unusedHackers
        gameDao.updateUnusedHackers(++unusedHackers)
    }

    // Activates a single unused hacker
    suspend fun activateHacker() {
        val inventoryData = inventoryDataFlow.first()
        var activeHackers = inventoryData.activeHackers
        var unusedHackers = inventoryData.unusedHackers
        if (unusedHackers > 0) {
            gameDao.updateHackers(++activeHackers, --unusedHackers)
        }
    }

    // Adds a new crypto miner to the inventory
    suspend fun addUnusedCryptoMiner() {
        val inventoryData = inventoryDataFlow.first()
        var unusedCryptoMiners = inventoryData.unusedCryptoMiners
        gameDao.updateUnusedHackers(++unusedCryptoMiners)
    }

    // Activates a single unused crypto miner
    suspend fun activateCryptoMiner() {
        val inventoryData = inventoryDataFlow.first()
        var activeCryptoMiners = inventoryData.activeCryptoMiners
        var unusedCryptoMiners = inventoryData.unusedCryptoMiners
        if (unusedCryptoMiners > 0) {
            gameDao.updateHackers(++activeCryptoMiners, --unusedCryptoMiners)
        }
    }

    // Adds a new botnet to the inventory
    suspend fun addUnusedBotnet() {
        val inventoryData = inventoryDataFlow.first()
        var unusedBotnets = inventoryData.unusedBotnets
        gameDao.updateUnusedHackers(++unusedBotnets)
    }

    // Activates a single unused botnet
    suspend fun activateBotnet() {
        val inventoryData = inventoryDataFlow.first()
        var activeBotnets = inventoryData.activeBotnets
        var unusedBotnets = inventoryData.unusedBotnets
        if (unusedBotnets > 0) {
            gameDao.updateHackers(++activeBotnets, --unusedBotnets)
        }
    }

    // Adds a new boost to the inventory
    suspend fun addBoost() {
        var boosts = gameDao.getInventory().first().boosts
        gameDao.updateBoosts(++boosts)
    }

    // Activates a single boost
    suspend fun activateBoost() {
        var boosts = gameDao.getInventory().first().boosts
        if(boosts > 0) {
            //TODO add real boost duration
            val activeUntil = System.currentTimeMillis() + 100_000;
            gameDao.updateBoostActivation(--boosts, activeUntil);
        }
    }

}
