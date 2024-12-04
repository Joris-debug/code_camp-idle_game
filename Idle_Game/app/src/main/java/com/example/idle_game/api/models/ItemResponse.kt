package com.example.idle_game.api.models

import com.example.idle_game.data.database.models.ShopData
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ItemResponse(
    @Json(name = "name") val name: String,
    @Json(name = "cost") val cost: Int,
    @Json(name = "duration") val duration: Int?,
    @Json(name = "boost-factor") val boostFactor: Int?,
    @Json(name = "unit/sec") val unitPerSec: Int?,
    @Json(name = "multiplier") val multiplier: Double?
) {
    fun toShopData(): ShopData {
        return ShopData(
            name = name,
            cost = cost,
            duration = duration,
            boostFactor = boostFactor,
            unitPerSec = unitPerSec,
            multiplier = multiplier
        )
    }
}