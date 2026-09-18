package com.football.teammanager.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.football.teammanager.data.Player
import com.football.teammanager.viewmodel.PlayerViewModel

@Composable
fun AddPlayerScreen(viewModel: PlayerViewModel, modifier: Modifier = Modifier, onBack: () -> Unit, onSaved: () -> Unit) {
    PlayerFormScreen(
        title = "Add Player",
        initialPlayer = Player(name = "", age = 16, position = "Goalkeeper", jerseyNumber = 1, gamesPlayed = 0, gamesSubstituted = 0, goalsScored = 0, assists = 0),
        saveLabel = "Save Player",
        modifier = modifier,
        onBack = onBack,
        onSave = { player -> viewModel.viewModelScopeSave(player, onSaved) }
    )
}
