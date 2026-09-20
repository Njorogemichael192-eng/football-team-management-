package com.football.teammanager.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FootballGreen = Color(0xFF123B2A)
private val FootballGreenDark = Color(0xFF0C2A1F)
private val FootballAccent = Color(0xFF4CAF50)
private val SurfaceWhite = Color(0xFFF7F8F5)

private val LightColors = lightColorScheme(
    primary = FootballGreen,
    secondary = FootballGreenDark,
    tertiary = FootballAccent,
    background = SurfaceWhite,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = FootballGreenDark,
    onSurface = FootballGreenDark
)

@Composable
fun FootballTeamManagerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content
    )
}
