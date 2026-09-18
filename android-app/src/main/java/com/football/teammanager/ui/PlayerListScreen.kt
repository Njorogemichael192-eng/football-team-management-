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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.football.teammanager.data.Player
import com.football.teammanager.viewmodel.PlayerUiState
import com.football.teammanager.viewmodel.PlayerViewModel

@Composable
fun PlayerListScreen(viewModel: PlayerViewModel, modifier: Modifier = Modifier, onAddPlayer: () -> Unit, onPlayerSelected: (Player) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("Football Team Manager") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddPlayer) { Icon(Icons.Default.Add, contentDescription = "Add player") }
        }
    ) { paddingValues ->
        Column(Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                label = { Text("Search by name") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )
            when (state) {
                PlayerUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
                is PlayerUiState.Error -> Text((state as PlayerUiState.Error).message)
                is PlayerUiState.Ready -> {
                    val players = (state as PlayerUiState.Ready).players.filter { it.name.contains(searchQuery, ignoreCase = true) }
                    if (players.isEmpty()) {
                        Text(if (searchQuery.isBlank()) "No players added yet." else "No matching players.", Modifier.padding(top = 16.dp))
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(players, key = { it.id }) { player -> PlayerCard(player, onPlayerSelected) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerCard(player: Player, onPlayerSelected: (Player) -> Unit) {
    Card(Modifier.fillMaxWidth().clickable { onPlayerSelected(player) }) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(player.name, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                Text("#${player.jerseyNumber}")
            }
            Text("${player.position} | Games played: ${player.gamesPlayed} | Goals: ${player.goalsScored}")
        }
    }
}

@Composable
private fun PlayerViewModel.collectAsStateWithLifecycleCompat() = androidx.compose.runtime.collectAsState(uiState)
