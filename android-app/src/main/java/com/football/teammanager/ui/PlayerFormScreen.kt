package com.football.teammanager.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.football.teammanager.data.Player
import com.football.teammanager.data.Team
import com.football.teammanager.data.positions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerFormScreen(
    title: String,
    initialPlayer: Player,
    saveLabel: String,
    teams: List<Team> = emptyList(),
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onSave: (Player) -> Unit
) {
    var name by remember { mutableStateOf(initialPlayer.name) }
    var age by remember { mutableStateOf(initialPlayer.age.toString()) }
    var position by remember { mutableStateOf(initialPlayer.position.ifBlank { positions.first() }) }
    var jerseyNumber by remember { mutableStateOf(initialPlayer.jerseyNumber.toString()) }
    var gamesPlayed by remember { mutableStateOf(initialPlayer.gamesPlayed.toString()) }
    var gamesSubstituted by remember { mutableStateOf(initialPlayer.gamesSubstituted.toString()) }
    var goalsScored by remember { mutableStateOf(initialPlayer.goalsScored.toString()) }
    var assists by remember { mutableStateOf(initialPlayer.assists.toString()) }
    var selectedTeamId by remember { mutableStateOf(initialPlayer.teamId ?: "") }
    var error by remember { mutableStateOf<String?>(null) }
    var positionExpanded by remember { mutableStateOf(false) }
    var teamExpanded by remember { mutableStateOf(false) }
    val teamOptions = listOf("Unassigned") + teams.map { it.name }

    fun number(value: String) = value.toIntOrNull()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(name, { name = it }, Modifier.fillMaxWidth(), label = { Text("Name") }, isError = error?.contains("Name") == true)
            OutlinedTextField(age, { age = it }, Modifier.fillMaxWidth(), label = { Text("Age") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            ExposedDropdownMenuBox(expanded = positionExpanded, onExpandedChange = { positionExpanded = !positionExpanded }) {
                OutlinedTextField(position, {}, Modifier.fillMaxWidth().menuAnchor(), readOnly = true, label = { Text("Position") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(positionExpanded) })
                ExposedDropdownMenu(expanded = positionExpanded, onDismissRequest = { positionExpanded = false }) {
                    positions.forEach { option -> DropdownMenuItem(text = { Text(option) }, onClick = { position = option; positionExpanded = false }) }
                }
            }
            NumberField("Jersey number", jerseyNumber) { jerseyNumber = it }
            NumberField("Games played", gamesPlayed) { gamesPlayed = it }
            NumberField("Games substituted", gamesSubstituted) { gamesSubstituted = it }
            NumberField("Goals scored", goalsScored) { goalsScored = it }
            NumberField("Assists", assists) { assists = it }
            ExposedDropdownMenuBox(expanded = teamExpanded, onExpandedChange = { teamExpanded = !teamExpanded }) {
                OutlinedTextField(
                    value = if (selectedTeamId.isBlank()) "Unassigned" else teams.firstOrNull { it.id == selectedTeamId }?.name ?: "Unassigned",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    label = { Text("Team") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(teamExpanded) }
                )
                ExposedDropdownMenu(expanded = teamExpanded, onDismissRequest = { teamExpanded = false }) {
                    listOf("Unassigned") + teams.map { it.name }.forEach { option ->
                        DropdownMenuItem(text = { Text(option) }, onClick = {
                            selectedTeamId = if (option == "Unassigned") "" else teams.first { it.name == option }.id
                            teamExpanded = false
                        })
                    }
                }
            }
            if (error != null) Text(error!!, color = androidx.compose.material3.MaterialTheme.colorScheme.error)
            Button(onClick = {
                val parsedAge = number(age)
                val parsedJersey = number(jerseyNumber)
                val parsedGames = number(gamesPlayed)
                val parsedSubstituted = number(gamesSubstituted)
                val parsedGoals = number(goalsScored)
                val parsedAssists = number(assists)
                error = when {
                    name.isBlank() -> "Name is required."
                    parsedAge !in 16..60 -> "Age must be between 16 and 60."
                    parsedJersey !in 1..99 -> "Jersey number must be between 1 and 99."
                    listOf(parsedGames, parsedSubstituted, parsedGoals, parsedAssists).any { it == null || it < 0 } -> "Statistics must be zero or greater."
                    else -> null
                }
                if (error == null) onSave(initialPlayer.copy(
                    name = name.trim(),
                    age = parsedAge!!,
                    position = position,
                    jerseyNumber = parsedJersey!!,
                    gamesPlayed = parsedGames!!,
                    gamesSubstituted = parsedSubstituted!!,
                    goalsScored = parsedGoals!!,
                    assists = parsedAssists!!,
                    teamId = selectedTeamId.ifBlank { null }
                ))
            }, Modifier.fillMaxWidth()) { Text(saveLabel) }
        }
    }
}

@Composable
private fun NumberField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(value, { input -> onValueChange(input.filter(Char::isDigit)) }, Modifier.fillMaxWidth(), label = { Text(label) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
}
