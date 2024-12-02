package com.example.idle_game.data.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class InventoryData (
    @PrimaryKey(autoGenerate = false)
    val uid: Int = 1,
    @ColumnInfo(name = "bitcoins") val bitcoins: Int = 0,
    @ColumnInfo(name = "active_hackers") val activeHackers: Int = 0,
    @ColumnInfo(name = "active_crypto_miners") val activeCryptoMiners: Int = 0,
    @ColumnInfo(name = "active_botnets") val activeBotnets: Int = 0,
    @ColumnInfo(name = "unused_hackers") val unusedHackers: Int = 0,
    @ColumnInfo(name = "unused_crypto_miners") val unusedCryptoMiners: Int = 0,
    @ColumnInfo(name = "unused_botnets") val unusedBotnets: Int = 0,
    @ColumnInfo(name = "upgrade_lvl_2") val upgradeLvl2: Int = 0,
    @ColumnInfo(name = "upgrade_lvl_3") val upgradeLvl3: Int = 0,
    @ColumnInfo(name = "upgrade_lvl_4") val upgradeLvl4: Int = 0,
    @ColumnInfo(name = "upgrade_lvl_5") val upgradeLvl5: Int = 0,
    @ColumnInfo(name = "low_boosts") val lowBoosts: Int = 0,
    @ColumnInfo(name = "medium_boosts") val mediumBoosts: Int = 0,
    @ColumnInfo(name = "high_boosts") val highBoosts: Int = 0,
    @ColumnInfo(name = "active_boost_type") val activeBoostType: Int = 0, // 1, 2 or 3
    @ColumnInfo(name = "boost_active_until") val boostActiveUntil: Long = 0
)