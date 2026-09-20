package com.football.teammanager.model

import kotlinx.serialization.Serializable

@Serializable
data class Coach(
    val name: String = "",
    val age: Int = 0,
    val phone: String? = null,
    val gamesManaged: Int = 0,
    val gamesWon: Int = 0,
    val gamesDrawn: Int = 0,
    val gamesLost: Int = 0
) {
    fun winRate(): Double = if (gamesManaged == 0) 0.0 else gamesWon.toDouble() / gamesManaged.toDouble() * 100.0
}
