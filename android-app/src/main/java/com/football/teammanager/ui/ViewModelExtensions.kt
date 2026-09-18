package com.football.teammanager.ui

import androidx.lifecycle.viewModelScope
import com.football.teammanager.data.Player
import com.football.teammanager.viewmodel.PlayerViewModel
import kotlinx.coroutines.launch

fun PlayerViewModel.viewModelScopeSave(player: Player, onSuccess: () -> Unit) {
    viewModelScope.launch {
        savePlayer(player).onSuccess { onSuccess() }
    }
}
