package com.football.teammanager.data

import kotlinx.serialization.Serializable

@Serializable
data class Team(
    val id: String = "",
    val name: String = "",
    val coach: Coach = Coach(),
    val createdAt: String = ""
)
