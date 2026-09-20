package com.football.teammanager.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.football.teammanager.data.Player
import com.football.teammanager.data.Team
import com.football.teammanager.viewmodel.PlayerViewModel
import com.football.teammanager.viewmodel.TeamViewModel
import kotlinx.coroutines.flow.collectLatest

private enum class Tab { DASHBOARD, TEAMS, PLAYERS }

private sealed interface Screen {
    data object List : Screen
    data object Add : Screen
    data class Details(val player: Player) : Screen
    data class Edit(val player: Player) : Screen
}

@Composable
fun FootballTeamManagerApp(playerViewModel: PlayerViewModel, teamViewModel: TeamViewModel) {
    var currentTab by remember { mutableStateOf(Tab.DASHBOARD) }
    var playerScreen by remember { mutableStateOf<Screen>(Screen.List) }
    var addTeamScreen by remember { mutableStateOf(false) }
    var selectedTeam by remember { mutableStateOf<Team?>(null) }
    var editingTeam by remember { mutableStateOf<Team?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val teams by teamViewModel.uiState.collectAsState()
    val players by playerViewModel.uiState.collectAsState()

    LaunchedEffect(playerViewModel) {
        playerViewModel.message.collectLatest { message ->
            if (message != null) {
                snackbarHostState.showSnackbar(message)
                playerViewModel.clearMessage()
            }
        }
    }
    LaunchedEffect(teamViewModel) {
        teamViewModel.message.collectLatest { message ->
            if (message != null) {
                snackbarHostState.showSnackbar(message)
                teamViewModel.clearMessage()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentTab == Tab.DASHBOARD,
                    onClick = { currentTab = Tab.DASHBOARD },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                    label = { Text("Dashboard") }
                )
                NavigationBarItem(
                    selected = currentTab == Tab.TEAMS,
                    onClick = { currentTab = Tab.TEAMS },
                    icon = { Icon(Icons.Default.Groups, contentDescription = "Teams") },
                    label = { Text("Teams") }
                )
                NavigationBarItem(
                    selected = currentTab == Tab.PLAYERS,
                    onClick = { currentTab = Tab.PLAYERS },
                    icon = { Icon(Icons.Default.People, contentDescription = "Players") },
                    label = { Text("Players") }
                )
            }
        }
    ) { paddingValues ->
        when (currentTab) {
            Tab.DASHBOARD -> DashboardScreen(
                teams = (teams as? com.football.teammanager.viewmodel.TeamUiState.Ready)?.teams ?: emptyList(),
                players = (players as? com.football.teammanager.viewmodel.PlayerUiState.Ready)?.players ?: emptyList(),
                modifier = Modifier.padding(paddingValues),
                onAddTeam = { addTeamScreen = true },
                onAddPlayer = { currentTab = Tab.PLAYERS },
                onTeamSelected = { selectedTeam = it; currentTab = Tab.TEAMS }
            )
            Tab.TEAMS -> {
                if (addTeamScreen) {
                    AddTeamScreen(
                        viewModel = teamViewModel,
                        modifier = Modifier.padding(paddingValues),
                        onBack = { addTeamScreen = false },
                        onSaved = {
                            addTeamScreen = false
                            currentTab = Tab.TEAMS
                            teamViewModel.loadTeams()
                        }
                    )
                } else if (editingTeam != null) {
                    EditTeamScreen(
                        viewModel = teamViewModel,
                        team = editingTeam!!,
                        modifier = Modifier.padding(paddingValues),
                        onBack = { editingTeam = null },
                        onSaved = {
                            editingTeam = null
                            currentTab = Tab.TEAMS
                            teamViewModel.loadTeams()
                        }
                    )
                } else if (selectedTeam != null) {
                    TeamDetailsScreen(
                        team = selectedTeam!!,
                        players = (players as? com.football.teammanager.viewmodel.PlayerUiState.Ready)?.players ?: emptyList(),
                        modifier = Modifier.padding(paddingValues),
                        onBack = { selectedTeam = null },
                        onEdit = { editingTeam = selectedTeam; selectedTeam = null },
                        onDelete = {
                            selectedTeam = null
                            currentTab = Tab.TEAMS
                            teamViewModel.loadTeams()
                        },
                        onAddPlayer = { currentTab = Tab.PLAYERS }
                    )
                } else {
                    TeamsScreen(
                        teams = (teams as? com.football.teammanager.viewmodel.TeamUiState.Ready)?.teams ?: emptyList(),
                        players = (players as? com.football.teammanager.viewmodel.PlayerUiState.Ready)?.players ?: emptyList(),
                        modifier = Modifier.padding(paddingValues),
                        onAddTeam = { addTeamScreen = true },
                        onTeamSelected = { selectedTeam = it },
                        onEditTeam = { editingTeam = it }
                    )
                }
            }
            Tab.PLAYERS -> when (val currentPlayerScreen = playerScreen) {
                Screen.List -> PlayerListScreen(
                    viewModel = playerViewModel,
                    teams = (teams as? com.football.teammanager.viewmodel.TeamUiState.Ready)?.teams ?: emptyList(),
                    modifier = Modifier.padding(paddingValues),
                    onAddPlayer = { playerScreen = Screen.Add },
                    onPlayerSelected = { playerScreen = Screen.Details(it) }
                )
                Screen.Add -> AddPlayerScreen(
                    viewModel = playerViewModel,
                    teams = (teams as? com.football.teammanager.viewmodel.TeamUiState.Ready)?.teams ?: emptyList(),
                    modifier = Modifier.padding(paddingValues),
                    onBack = { playerScreen = Screen.List },
                    onSaved = { playerScreen = Screen.List }
                )
                is Screen.Details -> PlayerDetailsScreen(
                    player = currentPlayerScreen.player,
                    teams = (teams as? com.football.teammanager.viewmodel.TeamUiState.Ready)?.teams ?: emptyList(),
                    modifier = Modifier.padding(paddingValues),
                    onBack = { playerScreen = Screen.List },
                    onEdit = { playerScreen = Screen.Edit(currentPlayerScreen.player) },
                    onDelete = {
                        playerViewModel.deletePlayer(currentPlayerScreen.player.id)
                        playerScreen = Screen.List
                    }
                )
                is Screen.Edit -> EditPlayerScreen(
                    player = currentPlayerScreen.player,
                    viewModel = playerViewModel,
                    teams = (teams as? com.football.teammanager.viewmodel.TeamUiState.Ready)?.teams ?: emptyList(),
                    modifier = Modifier.padding(paddingValues),
                    onBack = { playerScreen = Screen.Details(currentPlayerScreen.player) },
                    onSaved = { updated -> playerScreen = Screen.Details(updated) }
                )
            }
        }
    }
}
