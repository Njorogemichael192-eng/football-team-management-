package com.football.teammanager.model

import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val id: String = "",
    val name: String,
    val age: Int,
    val position: String,
    val jerseyNumber: Int,
    val gamesPlayed: Int,
    val gamesSubstituted: Int,
    val goalsScored: Int,
    val assists: Int,
    val teamId: String? = null
)

val allowedPositions = setOf("Goalkeeper", "Defender", "Midfielder", "Forward")
