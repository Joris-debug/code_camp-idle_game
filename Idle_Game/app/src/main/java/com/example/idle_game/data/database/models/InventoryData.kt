package com.example.idle_game.data.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity
data class InventoryData (
    @PrimaryKey(autoGenerate = false)
    val uid: Int = 1,
    @Json(name = "bitcoins") val bitcoins: Int,
    @Json(name = "active_hackers") val activeHackers: Int,
    @Json(name = "active_crypto_miners") val activeCryptoMiners: Int,
    @Json(name = "active_botnets") val activeBotnets: Int,
    @Json(name = "unused_hackers") val unusedHackers: Int,
    @Json(name = "unused_crypto_miners") val unusedCryptoMiners: Int,
    @Json(name = "unused_botnets") val unusedBotnets: Int,
    @Json(name = "boosts") val boosts: Int,
)