package com.example.idle_game.data.repositories

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.util.Log
import com.example.idle_game.api.GameApi
import com.example.idle_game.api.models.ItemResponse
import com.example.idle_game.api.models.ScoreResponse
import com.example.idle_game.api.models.SetScoreRequest
import com.example.idle_game.api.models.UserCredentialsRequest
import com.example.idle_game.data.database.GameDao
import com.example.idle_game.data.database.models.InventoryData
import com.example.idle_game.data.database.models.PlayerData
import com.example.idle_game.data.database.models.ScoreBoardData
import com.example.idle_game.data.database.models.ShopData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import kotlin.math.max

class GameRepository(
    private val api: GameApi,
    private val gameDao: GameDao,
    private val sharedPreferences: SharedPreferences
) {
    private val playerDataFlow = gameDao.getPlayer()
    private val inventoryDataFlow = gameDao.getInventory()
    private val shopDataFlow = gameDao.getShop()
    private val scoreBoardDataFlow = gameDao.getScoreBoard()

    fun getPlayerDataFlow(): Flow<PlayerData> {
        return playerDataFlow
    }

    fun getInventoryDataFlow(): Flow<InventoryData> {
        return inventoryDataFlow
    }

    fun getShopDataFlow(): Flow<List<ShopData>> {
        return shopDataFlow
    }

    fun getScoreBoardDataFlow(): Flow<List<ScoreBoardData>> {
        return scoreBoardDataFlow
    }

    suspend fun signUp(username: String, password: String, onFailure: () -> Unit = {}): Boolean {
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
            return true
        } catch (e: HttpException) {
            onFailure()
            return false
        }
    }

    suspend fun signIn(username: String, password: String, onFailure: () -> Unit = {}): Boolean {
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
            Log.e("Test", "true")
            return true
        } catch (e: HttpException) {
            onFailure()
            return false
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
    suspend fun login(onFailure: () -> Unit = {}): Boolean {
        val playerData = playerDataFlow.first()
        try {
            val resp = api.login(playerData.refreshToken)
            val accessToken = sharedPreferences.getString("access_token", null)
            if (accessToken != null) {
                gameDao.updateAccessToken(accessToken)
            }
            return true
        } catch (e: Exception) {
            onFailure()
            return false
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
            val score: Long = inventoryData.bitcoins + inventoryData.issuedBitcoins
            val setScoreRequest = SetScoreRequest(
                username = playerData.username,
                score = if (score >= 0) {
                    score
                } else {
                    Long.MAX_VALUE
                }
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

    suspend fun addBitcoins(bitcoins: Long) {
        gameDao.addBitcoins(bitcoins)
        if (inventoryDataFlow.first().bitcoins < 0) {
            gameDao.setBitcoins(Long.MAX_VALUE)
        }
    }

    suspend fun issueBitcoins(bitcoins: Long) {
        if (inventoryDataFlow.first().bitcoins < bitcoins) {
            return
        }
        gameDao.issueBitcoins(bitcoins)
        if (inventoryDataFlow.first().issuedBitcoins < 0) {
            gameDao.setIssuedBitcoins(Long.MAX_VALUE)
        }
    }

    suspend fun setMiningTimestamp(timestamp: Long) {
        gameDao.setMiningTimestamp(timestamp)
    }

    // Adds new lvl 1 hackers to the inventory
    private suspend fun addNewHacker(amount: Int) {
        if (inventoryDataFlow.first().hackersLvl1 + amount > 0) {
            gameDao.addNewHacker(amount = amount)
        }
    }

    // Adds new lvl 1 crypto miners to the inventory
    private suspend fun addNewCryptoMiner(amount: Int) {
        if (inventoryDataFlow.first().cryptoMinersLvl1 + amount > 0) {
            gameDao.addNewCryptoMiner(amount = amount)
        }
    }

    // Adds new lvl 1 botnets to the inventory
    private suspend fun addNewBotnet(amount: Int) {
        if (inventoryDataFlow.first().botnetsLvl1 + amount > 0) {
            gameDao.addNewBotnet(amount = amount)
        }
    }

    // Uses a level k upgrade on a level k-1 hacker, if both exist
    private suspend fun upgradeHacker(upgradeLvl: Int, quantity: Int) {
        val inventory = inventoryDataFlow.first()
        val hLvl1 = inventory.hackersLvl1
        val hLvl2 = inventory.hackersLvl2
        val hLvl3 = inventory.hackersLvl3
        val hLvl4 = inventory.hackersLvl4
        val hLvl5 = inventory.hackersLvl5

        when (upgradeLvl) {
            1 -> {
                val upgrades = inventory.upgradeLvl2
                if (hLvl1 >= quantity && upgrades >= quantity) {
                    gameDao.setHackers(hLvl1 - quantity, hLvl2 + quantity, hLvl3, hLvl4, hLvl5)
                }
            }

            2 -> {
                val upgrades = inventory.upgradeLvl3
                if (hLvl2 >= quantity && upgrades >= quantity) {
                    gameDao.setHackers(hLvl1, hLvl2 - quantity, hLvl3 + quantity, hLvl4, hLvl5)
                }
            }

            3 -> {
                val upgrades = inventory.upgradeLvl4
                if (hLvl3 >= quantity && upgrades >= quantity) {
                    gameDao.setHackers(hLvl1, hLvl2, hLvl3 - quantity, hLvl4 + quantity, hLvl5)
                }
            }

            4 -> {
                val upgrades = inventory.upgradeLvl5
                if (hLvl4 >= quantity && upgrades >= quantity) {
                    gameDao.setHackers(hLvl1, hLvl2, hLvl3, hLvl4 - quantity, hLvl5 + quantity)
                }
            }

            else -> {
                println("Invalid upgrade level: $upgradeLvl")
            }
        }
    }

    // Uses a level k upgrade on a level k-1 crypto miner, if both exist
    private suspend fun upgradeCryptoMiner(upgradeLvl: Int, quantity: Int) {
        val inventory = inventoryDataFlow.first()
        val cmLvl1 = inventory.cryptoMinersLvl1
        val cmLvl2 = inventory.cryptoMinersLvl2
        val cmLvl3 = inventory.cryptoMinersLvl3
        val cmLvl4 = inventory.cryptoMinersLvl4
        val cmLvl5 = inventory.cryptoMinersLvl5

        when (upgradeLvl) {
            1 -> {
                val upgrades = inventory.upgradeLvl2
                if (cmLvl1 >= quantity && upgrades >= quantity) {
                    gameDao.setCryptoMiners(cmLvl1 - quantity, cmLvl2 + quantity, cmLvl3, cmLvl4, cmLvl5)
                }
            }

            2 -> {
                val upgrades = inventory.upgradeLvl3
                if (cmLvl2 >= quantity && upgrades >= quantity) {
                    gameDao.setCryptoMiners(cmLvl1, cmLvl2 - quantity, cmLvl3 + quantity, cmLvl4, cmLvl5)
                }
            }

            3 -> {
                val upgrades = inventory.upgradeLvl4
                if (cmLvl3 >= quantity && upgrades >= quantity) {
                    gameDao.setCryptoMiners(cmLvl1, cmLvl2, cmLvl3 - quantity, cmLvl4 + quantity, cmLvl5)
                }
            }

            4 -> {
                val upgrades = inventory.upgradeLvl5
                if (cmLvl4 >= quantity && upgrades >= quantity) {
                    gameDao.setCryptoMiners(cmLvl1, cmLvl2, cmLvl3, cmLvl4 - quantity, cmLvl5 + quantity)
                }
            }

            else -> {
                println("Invalid upgrade level: $upgradeLvl")
            }
        }
    }

    // Uses a level k upgrade on a level k-1 botnet, if both exist
    private suspend fun upgradeBotnet(upgradeLvl: Int, quantity: Int) {
        val inventory = inventoryDataFlow.first()
        val bLvl1 = inventory.botnetsLvl1
        val bLvl2 = inventory.botnetsLvl2
        val bLvl3 = inventory.botnetsLvl3
        val bLvl4 = inventory.botnetsLvl4
        val bLvl5 = inventory.botnetsLvl5

        when (upgradeLvl) {
            1 -> {
                val upgrades = inventory.upgradeLvl2
                if (bLvl1 >= quantity && upgrades >= quantity) {
                    gameDao.setBotnets(bLvl1 - quantity, bLvl2 + quantity, bLvl3, bLvl4, bLvl5)
                }
            }

            2 -> {
                val upgrades = inventory.upgradeLvl3
                if (bLvl2 >= quantity && upgrades >= quantity) {
                    gameDao.setBotnets(bLvl1, bLvl2 - quantity, bLvl3 + quantity, bLvl4, bLvl5)
                }
            }

            3 -> {
                val upgrades = inventory.upgradeLvl4
                if (bLvl3 >= quantity && upgrades >= quantity) {
                    gameDao.setBotnets(bLvl1, bLvl2, bLvl3 - quantity, bLvl4 + quantity, bLvl5)
                }
            }

            4 -> {
                val upgrades = inventory.upgradeLvl5
                if (bLvl4 >= quantity && upgrades >= quantity) {
                    gameDao.setBotnets(bLvl1, bLvl2, bLvl3, bLvl4 - quantity, bLvl5 + quantity)
                }
            }

            else -> {
                println("Invalid upgrade level: $upgradeLvl")
            }
        }
    }

    private suspend fun addUpgradeLvl2(amount: Int) {
        val upgrades = inventoryDataFlow.first().upgradeLvl2
            gameDao.updateLvl2Upgrades(upgrades + amount)
    }

    private suspend fun addUpgradeLvl3(amount: Int) {
        val upgrades = inventoryDataFlow.first().upgradeLvl3
            gameDao.updateLvl3Upgrades(upgrades + amount)
    }

    private suspend fun addUpgradeLvl4(amount: Int) {
        val upgrades = inventoryDataFlow.first().upgradeLvl4
            gameDao.updateLvl4Upgrades(upgrades + amount)
    }

    @SuppressLint("SuspiciousIndentation")
    private suspend fun addUpgradeLvl5(amount: Int) {
        val upgrades = inventoryDataFlow.first().upgradeLvl5
            gameDao.updateLvl5Upgrades(upgrades + amount)
    }

    // Adds new low boosts to the inventory
    private suspend fun addLowBoost(amount: Int) {
        if (amount <= 0) {
            return
        }
        val upgrades = inventoryDataFlow.first().lowBoosts
        if (upgrades + amount > 0) {
            gameDao.updateLowBoosts(upgrades + amount)
        }
    }

    // Adds new medium boosts to the inventory
    private suspend fun addMediumBoost(amount: Int) {
        if (amount <= 0) {
            return
        }
        val upgrades = inventoryDataFlow.first().mediumBoosts
        if (upgrades + amount > 0) {
            gameDao.updateMediumBoosts(upgrades + amount)
        }
    }

    // Adds new high boosts to the inventory
    private suspend fun addHighBoost(amount: Int) {
        if (amount <= 0) {
            return
        }
        val upgrades = inventoryDataFlow.first().highBoosts
        if (upgrades + amount > 0) {
            gameDao.updateHighBoosts(upgrades + amount)
        }
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
        val inventory = inventoryDataFlow.first()
        var boosts = when (boostId) {
            LOW_BOOST_ID -> inventory.lowBoosts
            MEDIUM_BOOST_ID -> inventory.mediumBoosts
            HIGH_BOOST_ID -> inventory.highBoosts
            else -> 0
        }

        if (boosts > 0) {
            val activeUntil = System.currentTimeMillis() +
                    when (boostId) {
                        LOW_BOOST_ID -> gameDao.getLowBoostData().first().duration
                        MEDIUM_BOOST_ID -> gameDao.getMediumBoostData().first().duration
                        HIGH_BOOST_ID -> gameDao.getHighBoostData().first().duration
                        else -> 0
                    }!! * 60 * 1000 // From Min -> ms
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

    // Updating the database after buying items
    suspend fun buyItem(item: ShopData, amount: Int) {
        when (item.name) {
            "low Boost" -> addLowBoost(amount = amount)
            "medium Boost" -> addMediumBoost(amount = amount)
            "high Boost" -> addHighBoost(amount = amount)
            "low passive" -> addNewHacker(amount = amount)
            "medium passive" -> addNewCryptoMiner(amount = amount)
            "high passive" -> addNewBotnet(amount = amount)
            "upgrade lvl 2" -> addUpgradeLvl2(amount = max(0, amount))
            "upgrade lvl 3" -> addUpgradeLvl3(amount = max(0, amount))
            "upgrade lvl 4" -> addUpgradeLvl4(amount = max(0, amount))
            "upgrade lvl 5" -> addUpgradeLvl5(amount = max(0, amount))
        }
    }

    // Updating database after using items
    suspend fun useItem(item: ShopData, useOn: String, quantity: Int) {
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
                        upgradeHacker(1, quantity)
                    }

                    "Miner" -> {
                        upgradeCryptoMiner(1,quantity)
                    }

                    "BotNet" -> {
                        upgradeBotnet(1,quantity)
                    }
                }
                addUpgradeLvl2(-quantity)
            }
            "upgrade lvl 3" -> {
                when (useOn) {
                    "Hacker" -> {
                        upgradeHacker(2, quantity)
                    }

                    "Miner" -> {
                        upgradeCryptoMiner(2, quantity)
                    }

                    "BotNet" -> {
                        upgradeBotnet(2, quantity)
                    }
                }
                addUpgradeLvl3(-quantity)
            }
            "upgrade lvl 4" -> {
                when (useOn) {
                    "Hacker" -> {
                        upgradeHacker(3, quantity)
                    }

                    "Miner" -> {
                        upgradeCryptoMiner(3, quantity)
                    }

                    "BotNet" -> {
                        upgradeBotnet(3, quantity)
                    }
                }
                addUpgradeLvl4(-quantity)
            }
            "upgrade lvl 5" -> {
                when (useOn) {
                    "Hacker" -> {
                        upgradeHacker(4, quantity)
                    }

                    "Miner" -> {
                        upgradeCryptoMiner(4, quantity)
                    }

                    "BotNet" -> {
                        upgradeBotnet(4, quantity)
                    }
                }
                addUpgradeLvl5(-quantity)
            }
        }
    }

    suspend fun setBitcoin(bitcoins: Long) {
        gameDao.setBitcoins(bitcoins)
    }

    companion object {
        const val LOW_BOOST_ID = 1
        const val MEDIUM_BOOST_ID = 2
        const val HIGH_BOOST_ID = 3
    }
}