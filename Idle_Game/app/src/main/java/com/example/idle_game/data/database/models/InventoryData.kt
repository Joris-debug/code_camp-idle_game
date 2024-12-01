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
    @ColumnInfo(name = "boosts") val boosts: Int = 0,
    @ColumnInfo(name = "boost_active_until") val boostActiveUntil: Long = 0
)