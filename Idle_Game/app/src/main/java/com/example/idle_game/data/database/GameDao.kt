package com.example.idle_game.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.idle_game.data.database.models.InventoryData
import com.example.idle_game.data.database.models.PlayerData
import com.example.idle_game.data.database.models.ScoreBoardData
import com.example.idle_game.data.database.models.ShopData
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    @Query("SELECT * FROM playerdata WHERE uid = 1")
    fun getPlayer(): Flow<PlayerData>

    @Upsert
    suspend fun insertPlayer(player: PlayerData)

    @Query("SELECT COUNT(*) FROM playerdata")
    suspend fun getPlayersCount(): Int // Used to check if player data exists

    @Query("UPDATE playerdata SET refresh_token = :refreshToken")
    suspend fun updateRefreshToken(refreshToken: String)

    @Query("UPDATE playerdata SET access_token = :accessToken")
    suspend fun updateAccessToken(accessToken: String)

    @Query("SELECT * FROM inventorydata WHERE uid = 1")
    fun getInventory(): Flow<InventoryData>

    @Query("SELECT COUNT(*) FROM inventorydata")
    suspend fun getInventoriesCount(): Int // Used to check if inventory exists

    @Upsert
    suspend fun insertInventory(inventory: InventoryData)

    @Query("UPDATE inventorydata SET bitcoins = bitcoins + :bitcoins")
    suspend fun addBitcoins(bitcoins: Long)

    @Query("UPDATE inventorydata SET bitcoins = :bitcoins")
    suspend fun setBitcoins(bitcoins: Long)

    @Query(
        """
    UPDATE inventorydata
    SET bitcoins = bitcoins - :bitcoins,
        issued_bitcoins = issued_bitcoins + :bitcoins
    """
    )
    suspend fun issueBitcoins(bitcoins: Long)

    @Query("UPDATE inventorydata SET issued_bitcoins = :bitcoins")
    suspend fun setIssuedBitcoins(bitcoins: Long)

    @Query("UPDATE inventorydata SET hackers_lvl_1 = hackers_lvl_1 + :amount")
    suspend fun addNewHacker(amount: Int)

    @Query(
        """
    UPDATE inventorydata 
    SET hackers_lvl_1 = :hackersLvL1, 
        hackers_lvl_2 = :hackersLvL2, 
        hackers_lvl_3 = :hackersLvL3, 
        hackers_lvl_4 = :hackersLvL4,
        hackers_lvl_5 = :hackersLvL5
    """
    )
    suspend fun setHackers(
        hackersLvL1: Int,
        hackersLvL2: Int,
        hackersLvL3: Int,
        hackersLvL4: Int,
        hackersLvL5: Int
    )

    @Query("UPDATE inventorydata SET crypto_miners_lvl_1 = crypto_miners_lvl_1 + :amount")
    suspend fun addNewCryptoMiner(amount: Int)

    @Query(
        """
    UPDATE inventorydata 
    SET crypto_miners_lvl_1 = :cryptoMinersLvL1, 
        crypto_miners_lvl_2 = :cryptoMinersLvL2, 
        crypto_miners_lvl_3 = :cryptoMinersLvL3, 
        crypto_miners_lvl_4 = :cryptoMinersLvL4,
        crypto_miners_lvl_5 = :cryptoMinersLvL5
    """
    )
    suspend fun setCryptoMiners(
        cryptoMinersLvL1: Int,
        cryptoMinersLvL2: Int,
        cryptoMinersLvL3: Int,
        cryptoMinersLvL4: Int,
        cryptoMinersLvL5: Int
    )

    @Query("UPDATE inventorydata SET botnets_lvl_1 = botnets_lvl_1 + :amount")
    suspend fun addNewBotnet(amount: Int)

    @Query(
        """
    UPDATE inventorydata 
    SET botnets_lvl_1 = :botnetsLvL1, 
        botnets_lvl_2 = :botnetsLvL2, 
        botnets_lvl_3 = :botnetsLvL3, 
        botnets_lvl_4 = :botnetsLvL4,
        botnets_lvl_5 = :botnetsLvL5
    """
    )
    suspend fun setBotnets(
        botnetsLvL1: Int,
        botnetsLvL2: Int,
        botnetsLvL3: Int,
        botnetsLvL4: Int,
        botnetsLvL5: Int
    )

    @Query("UPDATE inventorydata SET low_boosts = :boosts")
    suspend fun updateLowBoosts(boosts: Int)

    @Query("UPDATE inventorydata SET medium_boosts = :boosts")
    suspend fun updateMediumBoosts(boosts: Int)

    @Query("UPDATE inventorydata SET high_boosts = :boosts")
    suspend fun updateHighBoosts(boosts: Int)

    @Query("UPDATE inventorydata SET active_boost_type = :type, boost_active_until = :until")
    suspend fun updateBoostActivation(type: Int, until: Long)

    @Query("UPDATE inventorydata SET upgrade_lvl_2 = :upgrades")
    suspend fun updateLvl2Upgrades(upgrades: Int)

    @Query("UPDATE inventorydata SET upgrade_lvl_3 = :upgrades")
    suspend fun updateLvl3Upgrades(upgrades: Int)

    @Query("UPDATE inventorydata SET upgrade_lvl_4 = :upgrades")
    suspend fun updateLvl4Upgrades(upgrades: Int)

    @Query("UPDATE inventorydata SET upgrade_lvl_5 = :upgrades")
    suspend fun updateLvl5Upgrades(upgrades: Int)

    @Query("SELECT * FROM scoreboarddata ORDER BY score DESC")
    fun getScoreBoard(): Flow<List<ScoreBoardData>>

    @Upsert
    suspend fun insertScoreBoard(player: ScoreBoardData)

    @Query("SELECT * FROM shopdata")
    fun getShop(): Flow<List<ShopData>>

    @Query("SELECT * FROM shopdata WHERE name = 'low passive'")
    fun getHackerShopData(): Flow<ShopData>

    @Query("SELECT * FROM shopdata WHERE name = 'medium passive'")
    fun getCryptoMinerShopData(): Flow<ShopData>

    @Query("SELECT * FROM shopdata WHERE name = 'high passive'")
    fun getBotnetShopData(): Flow<ShopData>

    @Query("SELECT * FROM shopdata WHERE name = 'low Boost'")
    fun getLowBoostData(): Flow<ShopData>

    @Query("SELECT * FROM shopdata WHERE name = 'medium Boost'")
    fun getMediumBoostData(): Flow<ShopData>

    @Query("SELECT * FROM shopdata WHERE name = 'high Boost'")
    fun getHighBoostData(): Flow<ShopData>

    @Query("SELECT * FROM shopdata WHERE name = 'upgrade lvl ' || :level")
    fun getUpgradeData(level: Int): Flow<ShopData>

    @Upsert
    suspend fun insertShop(item: ShopData)

    @Query("UPDATE InventoryData SET last_mining_timestamp = :timeStamp")
    suspend fun setMiningTimestamp(timeStamp: Long)
}