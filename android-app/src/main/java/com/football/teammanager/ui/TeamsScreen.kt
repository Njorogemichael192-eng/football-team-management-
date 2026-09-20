package com.football.teammanager.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.football.teammanager.data.Player
import com.football.teammanager.data.Team

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamsScreen(
    teams: List<Team>,
    players: List<Player>,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onAddTeam: () -> Unit,
    onTeamSelected: (Team) -> Unit,
    onEditTeam: (Team) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("Teams") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTeam) {
                Icon(Icons.Default.Add, contentDescription = "Add team")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
                return@Scaffold
            }
            if (errorMessage != null) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error)
                return@Scaffold
            }
            if (teams.isEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No teams yet.")
                    Text("Create your first team to get started.")
                    Button(onClick = onAddTeam) { Text("Add Team") }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(teams) { team ->
                        TeamCard(
                            team = team,
                            playerCount = players.count { it.teamId == team.id },
                            onTeamSelected = onTeamSelected,
                            onEditTeam = onEditTeam
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamCard(team: Team, playerCount: Int, onTeamSelected: (Team) -> Unit, onEditTeam: (Team) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onTeamSelected(team) }) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(team.name, style = MaterialTheme.typography.titleLarge)
                OutlinedButton(onClick = { onEditTeam(team) }) { Text("Edit") }
            }
            Text("Coach: ${team.coach.name.ifBlank { "Not assigned" }}")
            Text("Players: $playerCount")
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("W ${team.coach.gamesWon}")
                Text("D ${team.coach.gamesDrawn}")
                Text("L ${team.coach.gamesLost}")
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("View Team")
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            }
        }
    }
}
