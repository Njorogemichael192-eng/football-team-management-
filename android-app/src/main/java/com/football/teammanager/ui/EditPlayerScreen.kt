package com.football.teammanager.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.football.teammanager.data.Player
import com.football.teammanager.data.Team
import com.football.teammanager.viewmodel.PlayerViewModel

@Composable
fun EditPlayerScreen(
    player: Player,
    teams: List<Team> = emptyList(),
    viewModel: PlayerViewModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onSaved: (Player) -> Unit
) {
    PlayerFormScreen(
        title = "Edit Player",
        initialPlayer = player,
        saveLabel = "Save Changes",
        teams = teams,
        modifier = modifier,
        onBack = onBack,
        onSave = { updated -> viewModel.viewModelScopeSave(updated) { onSaved(updated) } }
    )
}
