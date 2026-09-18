package com.football.teammanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.football.teammanager.BuildConfig
import com.football.teammanager.data.ApiService
import com.football.teammanager.data.Player
import com.football.teammanager.data.PlayerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType

sealed interface PlayerUiState {
    data object Loading : PlayerUiState
    data class Ready(val players: List<Player>) : PlayerUiState
    data class Error(val message: String) : PlayerUiState
}

class PlayerViewModel : ViewModel() {
    private val repository = PlayerRepository(createApiService())
    private val _uiState = MutableStateFlow<PlayerUiState>(PlayerUiState.Loading)
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    init {
        loadPlayers()
    }

    fun loadPlayers() {
        viewModelScope.launch {
            _uiState.value = PlayerUiState.Loading
            runCatching { repository.getPlayers() }
                .onSuccess { _uiState.value = PlayerUiState.Ready(it) }
                .onFailure { _uiState.value = PlayerUiState.Error("Unable to load players.") }
        }
    }

    suspend fun getPlayer(id: String): Result<Player> = runCatching { repository.getPlayer(id) }

    suspend fun savePlayer(player: Player): Result<Unit> {
        return runCatching {
            if (player.id.isBlank()) repository.createPlayer(player) else repository.updatePlayer(player.id, player)
            _message.value = if (player.id.isBlank()) "Player saved successfully." else "Changes saved successfully."
            loadPlayers()
        }.onFailure { _message.value = "Unable to save player." }
    }

    fun deletePlayer(id: String) {
        viewModelScope.launch {
            runCatching { repository.deletePlayer(id) }
                .onSuccess {
                    _message.value = "Player deleted successfully."
                    loadPlayers()
                }
                .onFailure { _message.value = "Unable to delete player." }
        }
    }

    fun clearMessage() {
        _message.value = null
    }

    private fun createApiService(): ApiService {
        val json = Json { ignoreUnknownKeys = true }
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ApiService::class.java)
    }
}
