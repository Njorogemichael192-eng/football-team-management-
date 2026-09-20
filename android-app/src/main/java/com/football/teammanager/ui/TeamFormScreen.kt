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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.football.teammanager.data.Coach
import com.football.teammanager.data.Team
import com.football.teammanager.viewmodel.TeamViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTeamScreen(viewModel: TeamViewModel, modifier: Modifier = Modifier, onBack: () -> Unit, onSaved: () -> Unit) {
    val initialTeam = Team(name = "", coach = Coach())
    TeamFormScreen(
        title = "Add Team",
        initialTeam = initialTeam,
        modifier = modifier,
        onBack = onBack,
        onSave = { team ->
            viewModel.saveTeam(team).onSuccess { onSaved() }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTeamScreen(viewModel: TeamViewModel, team: Team, modifier: Modifier = Modifier, onBack: () -> Unit, onSaved: () -> Unit) {
    TeamFormScreen(
        title = "Edit Team",
        initialTeam = team,
        modifier = modifier,
        onBack = onBack,
        onSave = { updated ->
            viewModel.saveTeam(updated).onSuccess { onSaved() }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TeamFormScreen(
    title: String,
    initialTeam: Team,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onSave: suspend (Team) -> Unit
) {
    var name by remember { mutableStateOf(initialTeam.name) }
    var coachName by remember { mutableStateOf(initialTeam.coach.name) }
    var coachAge by remember { mutableStateOf(initialTeam.coach.age.toString()) }
    var coachPhone by remember { mutableStateOf(initialTeam.coach.phone.orEmpty()) }
    var gamesManaged by remember { mutableStateOf(initialTeam.coach.gamesManaged.toString()) }
    var gamesWon by remember { mutableStateOf(initialTeam.coach.gamesWon.toString()) }
    var gamesDrawn by remember { mutableStateOf(initialTeam.coach.gamesDrawn.toString()) }
    var gamesLost by remember { mutableStateOf(initialTeam.coach.gamesLost.toString()) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

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
            OutlinedTextField(value = name, onValueChange = { name = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Team Name") })
            OutlinedTextField(value = coachName, onValueChange = { coachName = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Coach Name") })
            OutlinedTextField(value = coachAge, onValueChange = { coachAge = it.filter(Char::isDigit) }, modifier = Modifier.fillMaxWidth(), label = { Text("Coach Age") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = coachPhone, onValueChange = { coachPhone = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Coach Phone") })
            OutlinedTextField(value = gamesManaged, onValueChange = { gamesManaged = it.filter(Char::isDigit) }, modifier = Modifier.fillMaxWidth(), label = { Text("Games Managed") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = gamesWon, onValueChange = { gamesWon = it.filter(Char::isDigit) }, modifier = Modifier.fillMaxWidth(), label = { Text("Games Won") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = gamesDrawn, onValueChange = { gamesDrawn = it.filter(Char::isDigit) }, modifier = Modifier.fillMaxWidth(), label = { Text("Games Drawn") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = gamesLost, onValueChange = { gamesLost = it.filter(Char::isDigit) }, modifier = Modifier.fillMaxWidth(), label = { Text("Games Lost") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

            if (error != null) Text(error!!)

            Button(onClick = {
                val parsedAge = coachAge.toIntOrNull() ?: 0
                val parsedManaged = gamesManaged.toIntOrNull() ?: 0
                val parsedWon = gamesWon.toIntOrNull() ?: 0
                val parsedDrawn = gamesDrawn.toIntOrNull() ?: 0
                val parsedLost = gamesLost.toIntOrNull() ?: 0
                val validation = when {
                    name.trim().isEmpty() -> "Team name is required."
                    coachName.trim().isEmpty() -> "Coach name is required."
                    parsedAge !in 18..100 -> "Coach age must be between 18 and 100."
                    parsedManaged < 0 || parsedWon < 0 || parsedDrawn < 0 || parsedLost < 0 -> "Statistics cannot be negative."
                    parsedWon + parsedDrawn + parsedLost > parsedManaged -> "Games won, drawn, and lost cannot exceed games managed."
                    else -> null
                }
                if (validation != null) {
                    error = validation
                    return@Button
                }
                scope.launch {
                    onSave(
                        initialTeam.copy(
                            id = initialTeam.id,
                            name = name.trim(),
                            coach = Coach(
                                name = coachName.trim(),
                                age = parsedAge,
                                phone = coachPhone.trim().ifBlank { null },
                                gamesManaged = parsedManaged,
                                gamesWon = parsedWon,
                                gamesDrawn = parsedDrawn,
                                gamesLost = parsedLost
                            ),
                            createdAt = initialTeam.createdAt.ifBlank { java.time.Instant.now().toString() }
                        )
                    )
                }
            }, modifier = Modifier.fillMaxWidth()) { Text("Save Team") }
        }
    }
}
