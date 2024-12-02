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

    companion object {
        const val LOW_BOOST_ID = 1
        const val MEDIUM_BOOST_ID = 2
        const val HIGH_BOOST_ID = 3
    }

    suspend fun signUp(username: String, password: String, onFailure: () -> Unit = {}) {
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
            onFailure()
        }
    }

    // Makes a server request and gets a new access_token
    suspend fun login(onFailure: () -> Unit = {}) {
        val playerData = playerDataFlow.first()
        try {
            val resp = api.login(playerData.refreshToken)
            val accessToken = sharedPreferences.getString("access_token", null)
            if(accessToken != null) {
                gameDao.updateAccessToken(accessToken)
            }
        } catch (e: HttpException) {
            onFailure()
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

    suspend fun addUpgradeLvl2() {
        var upgrades = gameDao.getInventory().first().upgradeLvl2
        gameDao.updateLvl2Upgrades(++upgrades)
    }

    suspend fun addUpgradeLvl3() {
        var upgrades = gameDao.getInventory().first().upgradeLvl3
        gameDao.updateLvl3Upgrades(++upgrades)
    }

    suspend fun addUpgradeLvl4() {
        var upgrades = gameDao.getInventory().first().upgradeLvl4
        gameDao.updateLvl4Upgrades(++upgrades)
    }

    suspend fun addUpgradeLvl5() {
        var upgrades = gameDao.getInventory().first().upgradeLvl5
        gameDao.updateLvl5Upgrades(++upgrades)
    }

    // Adds a new low boost to the inventory
    suspend fun addLowBoost() {
        var boosts = gameDao.getInventory().first().lowBoosts
        gameDao.updateLowBoosts(++boosts)
    }

    // Adds a new medium boost to the inventory
    suspend fun addMediumBoost() {
        var boosts = gameDao.getInventory().first().mediumBoosts
        gameDao.updateMediumBoosts(++boosts)
    }

    // Adds a new high boost to the inventory
    suspend fun addHighBoost() {
        var boosts = gameDao.getInventory().first().highBoosts
        gameDao.updateHighBoosts(++boosts)
    }

    // Activates a single low boost
    suspend fun activateLowBoost() {
        activateBoost(LOW_BOOST_ID);
    }

    // Activates a single medium boost
    suspend fun activateMediumBoost() {
        activateBoost(MEDIUM_BOOST_ID);
    }

    // Activates a single medium boost
    suspend fun activateHighBoost() {
        activateBoost(HIGH_BOOST_ID);
    }

    // Only used internally
    private suspend fun activateBoost(boostId: Int) {
        val inventory = gameDao.getInventory().first()
        var boosts = when(boostId) {
            LOW_BOOST_ID -> inventory.lowBoosts
            MEDIUM_BOOST_ID -> inventory.mediumBoosts
            HIGH_BOOST_ID -> inventory.highBoosts
            else -> 0
        }

        if (boosts > 0) {
            //TODO add real boost duration (read out of db)
            val activeUntil = System.currentTimeMillis() + 100_000
            gameDao.updateBoostActivation(boostId, activeUntil)

            when (boostId) {
                LOW_BOOST_ID -> {
                    gameDao.updateLowBoosts(--boosts)
                }
                MEDIUM_BOOST_ID -> {
                    gameDao.updateMediumBoosts(--boosts)
                }
                HIGH_BOOST_ID -> {
                    gameDao.updateHighBoosts(--boosts)
                }
            }
        }
    }

}
