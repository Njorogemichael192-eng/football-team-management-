package com.football.teammanager.model

import kotlin.test.Test
import kotlin.test.assertEquals

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
}
