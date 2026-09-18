package com.football.teammanager.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.football.teammanager.data.Player
import com.football.teammanager.viewmodel.PlayerViewModel
import kotlinx.coroutines.flow.collectLatest

private sealed interface Screen {
    data object List : Screen
    data object Add : Screen
    data class Details(val player: Player) : Screen
    data class Edit(val player: Player) : Screen
}

@Composable
fun FootballTeamManagerApp(viewModel: PlayerViewModel) {
    var screen by remember { mutableStateOf<Screen>(Screen.List) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.message.collectLatest { message ->
            if (message != null) {
                snackbarHostState.showSnackbar(message)
                viewModel.clearMessage()
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
        when (val currentScreen = screen) {
            Screen.List -> PlayerListScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(paddingValues),
                onAddPlayer = { screen = Screen.Add },
                onPlayerSelected = { screen = Screen.Details(it) }
            )
            Screen.Add -> AddPlayerScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(paddingValues),
                onBack = { screen = Screen.List },
                onSaved = { screen = Screen.List }
            )
            is Screen.Details -> PlayerDetailsScreen(
                player = currentScreen.player,
                modifier = Modifier.padding(paddingValues),
                onBack = { screen = Screen.List },
                onEdit = { screen = Screen.Edit(currentScreen.player) },
                onDelete = {
                    viewModel.deletePlayer(currentScreen.player.id)
                    screen = Screen.List
                }
            )
            is Screen.Edit -> EditPlayerScreen(
                player = currentScreen.player,
                viewModel = viewModel,
                modifier = Modifier.padding(paddingValues),
                onBack = { screen = Screen.Details(currentScreen.player) },
                onSaved = { updated -> screen = Screen.Details(updated) }
            )
        }
    }
}
