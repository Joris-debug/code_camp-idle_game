package com.example.idle_game.data.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ShopData(
    @PrimaryKey val name: String,
    @ColumnInfo(name = "cost") val cost: Int,
    @ColumnInfo(name = "duration") val duration: Int? = null,
    @ColumnInfo(name = "boost_factor") val boostFactor: Int? = null,
    @ColumnInfo(name = "unit_per_sec") val unitPerSec: Int? = null,
    @ColumnInfo(name = "multiplier") val multiplier: Double? = null
)