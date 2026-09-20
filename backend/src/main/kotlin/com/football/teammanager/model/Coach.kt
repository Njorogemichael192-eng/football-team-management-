package com.football.teammanager.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class Coach(
    val name: String = "",
    val age: Int = 0,
    val phone: String? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    val gamesManaged: Int = 0,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    val gamesWon: Int = 0,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    val gamesDrawn: Int = 0,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    val gamesLost: Int = 0
) {
    fun winRate(): Double = if (gamesManaged == 0) 0.0 else gamesWon.toDouble() / gamesManaged.toDouble() * 100.0
}
