package com.football.teammanager.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.football.teammanager.data.Player
import com.football.teammanager.data.Team

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailsScreen(
    team: Team,
    players: List<Player>,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAddPlayer: () -> Unit
) {
    val teamPlayers = players.filter { it.teamId == team.id }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(team.name) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("COACH", style = MaterialTheme.typography.labelLarge)
                    Text(team.coach.name)
                    Text("Age: ${team.coach.age}")
                    Text("Phone: ${team.coach.phone ?: "N/A"}")
                    Text("Games Managed: ${team.coach.gamesManaged}")
                    Text("Wins: ${team.coach.gamesWon}")
                    Text("Draws: ${team.coach.gamesDrawn}")
                    Text("Losses: ${team.coach.gamesLost}")
                    Text("Win Rate: ${String.format("%.0f%%", team.coach.winRate())}")
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onEdit, modifier = Modifier.weight(1f)) { Icon(Icons.Default.Edit, null); Text("Edit Team") }
                Button(onClick = onAddPlayer, modifier = Modifier.weight(1f)) { Text("Add Player") }
            }

            OutlinedButton(onClick = { showDeleteDialog = true }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Delete, null)
                Text("Delete Team")
            }

            Text("PLAYERS", style = MaterialTheme.typography.labelLarge)
            if (teamPlayers.isEmpty()) {
                Text("No players assigned to this team.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(teamPlayers) { player ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(player.name)
                                Text(player.position)
                                Text("#${player.jerseyNumber}")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete team?") },
            text = { Text("This action cannot be undone. Move or unassign players before deleting this team.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDelete()
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
