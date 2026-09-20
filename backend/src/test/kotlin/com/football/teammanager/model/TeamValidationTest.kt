package com.football.teammanager.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TeamValidationTest {
    @Test
    fun coachWinRateIsCalculatedCorrectly() {
        val coach = Coach(
            name = "John Smith",
            age = 45,
            phone = "0712345678",
            gamesManaged = 10,
            gamesWon = 6,
            gamesDrawn = 2,
            gamesLost = 2
        )

        assertEquals(60.0, coach.winRate())
    }

    @Test
    fun coachStatisticsAreIncludedWhenTheyAreZero() {
        val coach = Coach(name = "John Kamau", age = 35)

        val json = Json.encodeToString(coach)

        assertTrue(json.contains("\"gamesManaged\":0"))
        assertTrue(json.contains("\"gamesWon\":0"))
        assertTrue(json.contains("\"gamesDrawn\":0"))
        assertTrue(json.contains("\"gamesLost\":0"))
    }
}
