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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
fun DashboardScreen(
    teams: List<Team>,
    players: List<Player>,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onAddTeam: () -> Unit,
    onAddPlayer: () -> Unit,
    onTeamSelected: (Team) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("Dashboard") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Football Team Manager", style = MaterialTheme.typography.headlineSmall)

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                return@Scaffold
            }

            if (errorMessage != null) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error)
                return@Scaffold
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryCard("Teams", teams.size.toString(), Modifier.weight(1f))
                SummaryCard("Players", players.size.toString(), Modifier.weight(1f))
                SummaryCard("Coaches", teams.count { it.coach.name.isNotBlank() }.toString(), Modifier.weight(1f))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onAddTeam, modifier = Modifier.weight(1f)) { Text("+ Add Team") }
                Button(onClick = onAddPlayer, modifier = Modifier.weight(1f)) { Text("+ Add Player") }
            }

            Text("Teams", style = MaterialTheme.typography.titleLarge)
            if (teams.isEmpty()) {
                Text("No teams yet.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(teams) { team ->
                        TeamDashboardCard(
                            team = team,
                            playerCount = players.count { it.teamId == team.id },
                            onTeamSelected = onTeamSelected
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
private fun TeamDashboardCard(team: Team, playerCount: Int, onTeamSelected: (Team) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTeamSelected(team) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(team.name, style = MaterialTheme.typography.titleMedium)
            Text("Coach: ${team.coach.name.ifBlank { "Not assigned" }}")
            Text("Players: $playerCount")
            Text("Win rate: ${String.format("%.0f%%", team.coach.winRate())}")
        }
    }
}
