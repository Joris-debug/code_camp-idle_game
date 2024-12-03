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
    val shopDataFlow = gameDao.getShop()

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
            if (refreshToken != null) {
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
            if (accessToken != null) {
                gameDao.updateAccessToken(accessToken)
            }
        } catch (e: Throwable) {
            onFailure()
        }
    }

    // Makes a server request and fills the shop-data table
    suspend fun updateShop(onFailure: () -> Unit = {}) {
        val playerData = playerDataFlow.first()
        try {
            if (playerData.accessToken == null) {
                throw NullPointerException("accessToken can't be null")
            }
            val resp = api.getItems(playerData.accessToken)
            for (item in resp) {
                gameDao.insertShop(item.toShopData())
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

    // Adds a new lvl 1 hacker to the inventory
    suspend fun addNewHacker() {
        gameDao.addNewHacker()
    }

    // Uses a level k upgrade on a level k-1 hacker, if both exist
    suspend fun upgradeHacker(upgradeLvl: Int) {
        val inventory = inventoryDataFlow.first()
        val hLvl1 = inventory.hackersLvl1
        val hLvl2 = inventory.hackersLvl2
        val hLvl3 = inventory.hackersLvl3
        val hLvl4 = inventory.hackersLvl4
        val hLvl5 = inventory.hackersLvl5

        when (upgradeLvl) {
            1 -> {
                val upgrades = inventory.upgradeLvl2
                if (hLvl1 > 0 && upgrades > 0) {
                    gameDao.setHackers(hLvl1 - 1, hLvl2 + 1, hLvl3, hLvl4, hLvl5)
                }
            }
            2 -> {
                val upgrades = inventory.upgradeLvl3
                if (hLvl2 > 0 && upgrades > 0) {
                    gameDao.setHackers(hLvl1, hLvl2 - 1, hLvl3 + 1, hLvl4, hLvl5)
                }
            }
            3 -> {
                val upgrades = inventory.upgradeLvl4
                if (hLvl3 > 0 && upgrades > 0) {
                    gameDao.setHackers(hLvl1, hLvl2, hLvl3 - 1, hLvl4 + 1, hLvl5)
                }
            }
            4 -> {
                val upgrades = inventory.upgradeLvl5
                if (hLvl4 > 0 && upgrades > 0) {
                    gameDao.setHackers(hLvl1, hLvl2, hLvl3, hLvl4 - 1, hLvl5 + 1)
                }
            }
            else -> {
                println("Invalid upgrade level: $upgradeLvl")
            }
        }
    }

    // Uses a level k upgrade on a level k-1 crypto miner, if both exist
    suspend fun upgradeCryptoMiner(upgradeLvl: Int) {
        val inventory = inventoryDataFlow.first()
        val cmLvl1 = inventory.cryptoMinersLvl1
        val cmLvl2 = inventory.cryptoMinersLvl2
        val cmLvl3 = inventory.cryptoMinersLvl3
        val cmLvl4 = inventory.cryptoMinersLvl4
        val cmLvl5 = inventory.cryptoMinersLvl5

        when (upgradeLvl) {
            1 -> {
                val upgrades = inventory.upgradeLvl2
                if (cmLvl1 > 0 && upgrades > 0) {
                    gameDao.setHackers(cmLvl1 - 1, cmLvl2 + 1, cmLvl3, cmLvl4, cmLvl5)
                }
            }
            2 -> {
                val upgrades = inventory.upgradeLvl3
                if (cmLvl2 > 0 && upgrades > 0) {
                    gameDao.setHackers(cmLvl1, cmLvl2 - 1, cmLvl3 + 1, cmLvl4, cmLvl5)
                }
            }
            3 -> {
                val upgrades = inventory.upgradeLvl4
                if (cmLvl3 > 0 && upgrades > 0) {
                    gameDao.setHackers(cmLvl1, cmLvl2, cmLvl3 - 1, cmLvl4 + 1, cmLvl5)
                }
            }
            4 -> {
                val upgrades = inventory.upgradeLvl5
                if (cmLvl4 > 0 && upgrades > 0) {
                    gameDao.setHackers(cmLvl1, cmLvl2, cmLvl3, cmLvl4 - 1, cmLvl5 + 1)
                }
            }
            else -> {
                println("Invalid upgrade level: $upgradeLvl")
            }
        }
    }

    // Uses a level k upgrade on a level k-1 botnet, if both exist
    suspend fun upgradeBotnet(upgradeLvl: Int) {
        val inventory = inventoryDataFlow.first()
        val bLvl1 = inventory.botnetsLvl1
        val bLvl2 = inventory.botnetsLvl2
        val bLvl3 = inventory.botnetsLvl3
        val bLvl4 = inventory.botnetsLvl4
        val bLvl5 = inventory.botnetsLvl5

        when (upgradeLvl) {
            1 -> {
                val upgrades = inventory.upgradeLvl2
                if (bLvl1 > 0 && upgrades > 0) {
                    gameDao.setHackers(bLvl1 - 1, bLvl2 + 1, bLvl3, bLvl4, bLvl5)
                }
            }
            2 -> {
                val upgrades = inventory.upgradeLvl3
                if (bLvl2 > 0 && upgrades > 0) {
                    gameDao.setHackers(bLvl1, bLvl2 - 1, bLvl3 + 1, bLvl4, bLvl5)
                }
            }
            3 -> {
                val upgrades = inventory.upgradeLvl4
                if (bLvl3 > 0 && upgrades > 0) {
                    gameDao.setHackers(bLvl1, bLvl2, bLvl3 - 1, bLvl4 + 1, bLvl5)
                }
            }
            4 -> {
                val upgrades = inventory.upgradeLvl5
                if (bLvl4 > 0 && upgrades > 0) {
                    gameDao.setHackers(bLvl1, bLvl2, bLvl3, bLvl4 - 1, bLvl5 + 1)
                }
            }
            else -> {
                println("Invalid upgrade level: $upgradeLvl")
            }
        }
    }

    // Adds a new lvl 1 crypto miner to the inventory
    suspend fun addNewCryptoMiner() {
        gameDao.addNewCryptoMiner()
    }

    // Adds a new lvl 1 botnet to the inventory
    suspend fun addNewBotnet() {
        gameDao.addNewBotnet()
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
        var boosts = when (boostId) {
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
