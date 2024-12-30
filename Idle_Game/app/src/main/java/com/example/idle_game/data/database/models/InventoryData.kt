package com.example.idle_game.data.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity
data class InventoryData (
    @PrimaryKey(autoGenerate = false)
    val uid: Int = 1,
    @ColumnInfo(name = "bitcoins") val bitcoins: Long = 0,
    @ColumnInfo(name = "issued_bitcoins") val issuedBitcoins: Long = 0,
    @ColumnInfo(name = "hackers_lvl_1") val hackersLvl1: Int = 0,
    @ColumnInfo(name = "hackers_lvl_2") val hackersLvl2: Int = 0,
    @ColumnInfo(name = "hackers_lvl_3") val hackersLvl3: Int = 0,
    @ColumnInfo(name = "hackers_lvl_4") val hackersLvl4: Int = 0,
    @ColumnInfo(name = "hackers_lvl_5") val hackersLvl5: Int = 0,
    @ColumnInfo(name = "crypto_miners_lvl_1") val cryptoMinersLvl1: Int = 0,
    @ColumnInfo(name = "crypto_miners_lvl_2") val cryptoMinersLvl2: Int = 0,
    @ColumnInfo(name = "crypto_miners_lvl_3") val cryptoMinersLvl3: Int = 0,
    @ColumnInfo(name = "crypto_miners_lvl_4") val cryptoMinersLvl4: Int = 0,
    @ColumnInfo(name = "crypto_miners_lvl_5") val cryptoMinersLvl5: Int = 0,
    @ColumnInfo(name = "botnets_lvl_1") val botnetsLvl1: Int = 0,
    @ColumnInfo(name = "botnets_lvl_2") val botnetsLvl2: Int = 0,
    @ColumnInfo(name = "botnets_lvl_3") val botnetsLvl3: Int = 0,
    @ColumnInfo(name = "botnets_lvl_4") val botnetsLvl4: Int = 0,
    @ColumnInfo(name = "botnets_lvl_5") val botnetsLvl5: Int = 0,
    @ColumnInfo(name = "upgrade_lvl_2") val upgradeLvl2: Int = 0,
    @ColumnInfo(name = "upgrade_lvl_3") val upgradeLvl3: Int = 0,
    @ColumnInfo(name = "upgrade_lvl_4") val upgradeLvl4: Int = 0,
    @ColumnInfo(name = "upgrade_lvl_5") val upgradeLvl5: Int = 0,
    @ColumnInfo(name = "low_boosts") val lowBoosts: Int = 0,
    @ColumnInfo(name = "medium_boosts") val mediumBoosts: Int = 0,
    @ColumnInfo(name = "high_boosts") val highBoosts: Int = 0,
    @ColumnInfo(name = "active_boost_type") val activeBoostType: Int = 0, // 1, 2 or 3
    @ColumnInfo(name = "boost_active_until") val boostActiveUntil: Long = 0,
    @ColumnInfo(name = "last_mining_timestamp") val lastMiningTimestamp: Long = System.currentTimeMillis()
)