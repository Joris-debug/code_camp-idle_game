package com.example.idle_game.data.repositories

import android.content.SharedPreferences
import com.example.idle_game.api.GameApi
import com.example.idle_game.api.models.ItemResponse
import com.example.idle_game.api.models.ScoreResponse
import com.example.idle_game.api.models.SetScoreRequest
import com.example.idle_game.api.models.UserCredentialsRequest
import com.example.idle_game.data.database.GameDao
import com.example.idle_game.data.database.models.InventoryData
import com.example.idle_game.data.database.models.PlayerData
import com.example.idle_game.data.database.models.ShopData
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
    val scoreBoardDataFlow = gameDao.getScoreBoard()

    companion object {
        const val LOW_BOOST_ID = 1
        const val MEDIUM_BOOST_ID = 2
        const val HIGH_BOOST_ID = 3
    }

    suspend fun signUp(username: String, password: String, onFailure: () -> Unit = {}) {
        val userCredentialsRequest = UserCredentialsRequest(
            username = username,
            password = password
        )
        try {
            val resp = api.signUp(userCredentialsRequest)
            val refreshToken = sharedPreferences.getString("refresh_token", null)
            if (refreshToken != null) {
                val playerData = PlayerData(
                    username = username,
                    refreshToken = refreshToken,
                    accessToken = null
                )
                gameDao.insertPlayer(playerData)
                createNewInventory()
            }
        } catch (e: HttpException) {
            onFailure()
        }
    }

    suspend fun signIn(username: String, password: String, onFailure: () -> Unit = {}) {
        if (gameDao.getPlayersCount() != 0) { // Check for existing db entry
            if (gameDao.getPlayer().first().username != username) {
                createNewInventory()
            }
        }
        val userCredentialsRequest = UserCredentialsRequest(
            username = username,
            password = password
        )
        try {
            val resp = api.signIn(userCredentialsRequest)
            val refreshToken = sharedPreferences.getString("refresh_token", null)
            if (refreshToken != null) {
                val playerData = PlayerData(
                    username = username,
                    refreshToken = refreshToken,
                    accessToken = null
                )
                gameDao.insertPlayer(playerData)
                if (gameDao.getInventoriesCount() == 0) {
                    createNewInventory()
                }
            }
        } catch (e: HttpException) {
            onFailure()
        }
    }


    // Called on the settings-page
    suspend fun logout() {
        gameDao.updateRefreshToken("") // Default value for the refresh token
        /*
        * Important:
        * Warn user: Login in with an other account will make him lose all data
        * Info user: Restart the app to return to login page (or force him to do: auto restart app (bad practice) or load LoginView)
        */
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
        } catch (e: Exception) {
            onFailure()
        }
    }

    // Makes a server request and fills the shop-data table
    suspend fun updateShop(onFailure: () -> Unit = {}) {
        val playerData = playerDataFlow.first()
        var resp: List<ItemResponse> = emptyList()
        try {
            if (playerData.accessToken == null) {
                throw NullPointerException("accessToken can't be null")
            }
            try {
                resp = api.getItems(playerData.accessToken)
            } catch (e: HttpException) {
                if (e.code() == 490) {
                    // No valid access token
                    login()
                    resp = api.getItems(playerData.accessToken)
                }
            }
            for (item in resp) {
                gameDao.insertShop(item.toShopData())
            }
        } catch (e: Exception) {
            onFailure()
        }
    }

    // Makes a server request and fills the score-board-data table
    suspend fun fetchScoreBoard(onFailure: () -> Unit = {}) {
        val playerData = playerDataFlow.first()
        var resp: List<ScoreResponse> = emptyList()
        try {
            if (playerData.accessToken == null) {
                throw NullPointerException("accessToken can't be null")
            }
            try {
                resp = api.getScore(playerData.accessToken)
            } catch (e: HttpException) {
                if (e.code() == 490) {
                    // No valid access token
                    login()
                    resp = api.getScore(playerData.accessToken)
                }
            }
            for (player in resp) {
                gameDao.insertScoreBoard(player.toScoreBoardData())
            }
        } catch (e: Exception) {
            onFailure()
        }
    }

    // Makes a server request and puts the player score on the board
    // Call fetchScoreBoard afterwards to have the ScoreBoard updated
    suspend fun updateScoreBoard(onFailure: () -> Unit = {}) {
        try {
            val playerData = playerDataFlow.first()
            if (playerData.accessToken == null) {
                throw NullPointerException("accessToken can't be null")
            }

            val inventoryData = inventoryDataFlow.first()
            val setScoreRequest = SetScoreRequest(
                username = playerData.username,
                score = inventoryData.bitcoins + inventoryData.issuedBitcoins
            )
            try {
                api.postScore(
                    playerData.accessToken,
                    setScoreRequest = setScoreRequest
                )
            } catch (e: HttpException) {
                if (e.code() == 490) {
                    // No valid access token
                    login()
                    api.postScore(
                        playerData.accessToken,
                        setScoreRequest = setScoreRequest
                    )
                }
            }
        } catch (e: Exception) {
            onFailure()
        }
    }

    suspend fun getHackerShopData(): ShopData {
        return gameDao.getHackerShopData().first()
    }

    suspend fun getCryptoMinerShopData(): ShopData {
        return gameDao.getCryptoMinerShopData().first()
    }

    suspend fun getBotnetShopData(): ShopData {
        return gameDao.getBotnetShopData().first()
    }

    suspend fun getUpgradeData(level: Int): ShopData? {
        if (level < 2 || level > 5) {
            return null
        }
        return gameDao.getUpgradeData(level).first()
    }

    // Call this function before accessing the inventory for the first time
    private suspend fun createNewInventory() {
        gameDao.insertInventory(InventoryData())
    }

    // TODO add error handing if no inventory exists (all functions)
    suspend fun addBitcoins(bitcoins: Long) {
        if (bitcoins <= 0) {
            return
        }
        gameDao.addBitcoins(bitcoins)
    }

    suspend fun issueBitcoins(bitcoins: Long) {
        if (inventoryDataFlow.first().bitcoins < bitcoins) {
            return
        }
        gameDao.issueBitcoins(bitcoins)
    }

    suspend fun setMiningTimestamp(timestamp: Long) {
        gameDao.setMiningTimestamp(timestamp)
    }

    // Adds a new lvl 1 hacker to the inventory
    private suspend fun addNewHacker(amount: Int) {
        gameDao.addNewHacker(amount = amount)
    }

    // Uses a level k upgrade on a level k-1 hacker, if both exist
    private suspend fun upgradeHacker(upgradeLvl: Int) {
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
    private suspend fun upgradeCryptoMiner(upgradeLvl: Int) {
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
                    gameDao.setCryptoMiners(cmLvl1 - 1, cmLvl2 + 1, cmLvl3, cmLvl4, cmLvl5)
                }
            }
            2 -> {
                val upgrades = inventory.upgradeLvl3
                if (cmLvl2 > 0 && upgrades > 0) {
                    gameDao.setCryptoMiners(cmLvl1, cmLvl2 - 1, cmLvl3 + 1, cmLvl4, cmLvl5)
                }
            }
            3 -> {
                val upgrades = inventory.upgradeLvl4
                if (cmLvl3 > 0 && upgrades > 0) {
                    gameDao.setCryptoMiners(cmLvl1, cmLvl2, cmLvl3 - 1, cmLvl4 + 1, cmLvl5)
                }
            }
            4 -> {
                val upgrades = inventory.upgradeLvl5
                if (cmLvl4 > 0 && upgrades > 0) {
                    gameDao.setCryptoMiners(cmLvl1, cmLvl2, cmLvl3, cmLvl4 - 1, cmLvl5 + 1)
                }
            }
            else -> {
                println("Invalid upgrade level: $upgradeLvl")
            }
        }
    }

    // Uses a level k upgrade on a level k-1 botnet, if both exist
    private suspend fun upgradeBotnet(upgradeLvl: Int) {
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
                    gameDao.setBotnets(bLvl1 - 1, bLvl2 + 1, bLvl3, bLvl4, bLvl5)
                }
            }
            2 -> {
                val upgrades = inventory.upgradeLvl3
                if (bLvl2 > 0 && upgrades > 0) {
                    gameDao.setBotnets(bLvl1, bLvl2 - 1, bLvl3 + 1, bLvl4, bLvl5)
                }
            }
            3 -> {
                val upgrades = inventory.upgradeLvl4
                if (bLvl3 > 0 && upgrades > 0) {
                    gameDao.setBotnets(bLvl1, bLvl2, bLvl3 - 1, bLvl4 + 1, bLvl5)
                }
            }
            4 -> {
                val upgrades = inventory.upgradeLvl5
                if (bLvl4 > 0 && upgrades > 0) {
                    gameDao.setBotnets(bLvl1, bLvl2, bLvl3, bLvl4 - 1, bLvl5 + 1)
                }
            }
            else -> {
                println("Invalid upgrade level: $upgradeLvl")
            }
        }
    }

    // Adds a new lvl 1 crypto miner to the inventory
    private suspend fun addNewCryptoMiner(amount: Int) {
        gameDao.addNewCryptoMiner(amount = amount)
    }

    // Adds a new lvl 1 botnet to the inventory
    private suspend fun addNewBotnet(amount: Int) {
        gameDao.addNewBotnet(amount = amount)
    }

    private suspend fun addUpgradeLvl2(amount: Int) {
        val upgrades = gameDao.getInventory().first().upgradeLvl2
        gameDao.updateLvl2Upgrades(upgrades + amount)
    }

    private suspend fun addUpgradeLvl3(amount: Int) {
        val upgrades = gameDao.getInventory().first().upgradeLvl3
        gameDao.updateLvl3Upgrades(upgrades + amount)
    }

    private suspend fun addUpgradeLvl4(amount: Int) {
        val upgrades = gameDao.getInventory().first().upgradeLvl4
        gameDao.updateLvl4Upgrades(upgrades + amount)
    }

    private suspend fun addUpgradeLvl5(amount: Int) {
        val upgrades = gameDao.getInventory().first().upgradeLvl5
        gameDao.updateLvl5Upgrades(upgrades + amount)
    }

    // Adds a new low boost to the inventory
    private suspend fun addLowBoost(amount: Int) {
        val boosts = gameDao.getInventory().first().lowBoosts
        gameDao.updateLowBoosts(boosts + amount)
    }

    // Adds a new medium boost to the inventory
    private suspend fun addMediumBoost(amount: Int) {
        val boosts = gameDao.getInventory().first().mediumBoosts
        gameDao.updateMediumBoosts(boosts + amount)
    }

    // Adds a new high boost to the inventory
    private suspend fun addHighBoost(amount: Int) {
        val boosts = gameDao.getInventory().first().highBoosts
        gameDao.updateHighBoosts(boosts + amount)
    }

    // Activates a single low boost
    private suspend fun activateLowBoost() {
        activateBoost(LOW_BOOST_ID)
    }

    // Activates a single medium boost
    private suspend fun activateMediumBoost() {
        activateBoost(MEDIUM_BOOST_ID)
    }

    // Activates a single High boost
    private suspend fun activateHighBoost() {
        activateBoost(HIGH_BOOST_ID)
    }

    suspend fun isBoostActive(): Boolean {
        val inventory = inventoryDataFlow.first()
        if (inventory.activeBoostType > 0) {
            val now = System.currentTimeMillis()
            if (inventory.boostActiveUntil <= now) {
                gameDao.updateBoostActivation(0, 0)
                return false
            }
            return true
        }
        return false
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

    suspend fun getBoostFactor(): Int {
        val inventory = inventoryDataFlow.first()
        return when (inventory.activeBoostType) {
            LOW_BOOST_ID -> gameDao.getLowBoostData().first().boostFactor
            MEDIUM_BOOST_ID -> gameDao.getMediumBoostData().first().boostFactor
            HIGH_BOOST_ID -> gameDao.getHighBoostData().first().boostFactor
            else -> 1
        }!!
    }

    //Updating the database after buying items
    suspend fun buyItem(item: ShopData, amount: Int) {
        when (item.name) {
            "low Boost" -> addLowBoost(amount = amount)
            "medium Boost" -> addMediumBoost(amount = amount)
            "high Boost" -> addHighBoost(amount = amount)
            "low passive" -> addNewHacker(amount = amount)
            "medium passive" -> addNewCryptoMiner(amount = amount)
            "high passive" -> addNewBotnet(amount = amount)
            "upgrade lvl 2" -> addUpgradeLvl2(amount = amount)
            "upgrade lvl 3" -> addUpgradeLvl3(amount = amount)
            "upgrade lvl 4" -> addUpgradeLvl4(amount = amount)
            "upgrade lvl 5" -> addUpgradeLvl5(amount = amount)
        }
    }

    //Updating database after using items
    suspend fun useItem(item: ShopData, useOn: String) {
        if (!isBoostActive()) {
            when (item.name) {
                "low Boost" -> activateLowBoost()
                "medium Boost" -> activateMediumBoost()
                "high Boost" -> activateHighBoost()
            }
        }
        when (item.name) {
            "upgrade lvl 2" -> {
                when (useOn) {
                    "Hacker" -> {
                        upgradeHacker(1)
                    }
                    "Miner" -> {
                        upgradeCryptoMiner(1)
                    }
                    "BotNet" -> {
                        upgradeBotnet(1)
                    }
                }
                addUpgradeLvl2(-1)
            }

            "upgrade lvl 3" -> {
                when (useOn) {
                    "Hacker" -> {
                        upgradeHacker(2)
                    }
                    "Miner" -> {
                        upgradeCryptoMiner(2)
                    }
                    "BotNet" -> {
                        upgradeBotnet(2)
                    }
                }
                addUpgradeLvl3(-1)
            }

            "upgrade lvl 4" -> {
                when (useOn) {
                    "Hacker" -> {
                        upgradeHacker(3)
                    }
                    "Miner" -> {
                        upgradeCryptoMiner(3)
                    }
                    "BotNet" -> {
                        upgradeBotnet(3)
                    }
                }
                addUpgradeLvl4(-1)
            }

            "upgrade lvl 5" -> {
                when (useOn) {
                    "Hacker" -> {
                        upgradeHacker(4)
                    }
                    "Miner" -> {
                        upgradeCryptoMiner(4)
                    }
                    "BotNet" -> {
                        upgradeBotnet(4)
                    }
                }
                addUpgradeLvl5(-1)
            }
        }
    }

}
