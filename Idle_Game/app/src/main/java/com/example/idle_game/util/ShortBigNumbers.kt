package com.example.idle_game.util

import kotlin.math.log10
import kotlin.math.pow

/**
 * Converts a Long into a shorted version of this number.
 * E.g.: 10000 -> 10K
 * @return String
 */
fun shortBigNumbers(number: Long): String {
    val digits = if (number == 0L) 1 else log10(kotlin.math.abs(number).toDouble()).toInt() + 1
    if (digits > 15) { //1Qd
        return numberToString(number, 15, "Qd")
    } else if (digits > 12) { //1T
        return numberToString(number, 12, "T")
    } else if (digits > 9) { //1B
        return numberToString(number, 9, "B")
    } else if (digits > 6) { //1M
        return numberToString(number, 6, "M")
    } else if (digits > 3) { //1K
        return numberToString(number, 3, "K")
    }

    return "$number"
}

fun numberToString(number: Long, toDigit: Int, suffix: String): String {
    val shortedNumber: Double = number.toDouble() / (10.0.pow(toDigit))
    return String.format("%.2f$suffix", shortedNumber)
}