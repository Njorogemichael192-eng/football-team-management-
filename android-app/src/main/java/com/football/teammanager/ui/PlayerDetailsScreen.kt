package com.football.teammanager.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.football.teammanager.data.Player

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerDetailsScreen(player: Player, modifier: Modifier = Modifier, onBack: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Player Details") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )
        }
    ) { paddingValues ->
        Column(Modifier.fillMaxSize().padding(paddingValues).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(player.name, style = androidx.compose.material3.MaterialTheme.typography.headlineSmall)
            Detail("Age", player.age.toString())
            Detail("Position", player.position)
            Detail("Jersey number", player.jerseyNumber.toString())
            Detail("Games played", player.gamesPlayed.toString())
            Detail("Games substituted", player.gamesSubstituted.toString())
            Detail("Goals scored", player.goalsScored.toString())
            Detail("Assists", player.assists.toString())
            Button(onClick = onEdit, Modifier.fillMaxWidth()) { Icon(Icons.Default.Edit, null); Text("Edit") }
            OutlinedButton(onClick = onDelete, Modifier.fillMaxWidth()) { Icon(Icons.Default.Delete, null); Text("Delete") }
        }
    }
}

@Composable
private fun Detail(label: String, value: String) {
    Text("$label: $value")
}
