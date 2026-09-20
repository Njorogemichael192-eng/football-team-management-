package com.football.teammanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.football.teammanager.BuildConfig
import com.football.teammanager.data.ApiService
import com.football.teammanager.data.Team
import com.football.teammanager.data.TeamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

sealed interface TeamUiState {
    data object Loading : TeamUiState
    data class Ready(val teams: List<Team>) : TeamUiState
    data class Error(val message: String) : TeamUiState
}

class TeamViewModel : ViewModel() {
    private val repository = TeamRepository(createApiService())
    private val _uiState = MutableStateFlow<TeamUiState>(TeamUiState.Loading)
    val uiState: StateFlow<TeamUiState> = _uiState.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    init {
        loadTeams()
    }

    fun loadTeams() {
        viewModelScope.launch {
            _uiState.value = TeamUiState.Loading
            runCatching { repository.getTeams() }
                .onSuccess { _uiState.value = TeamUiState.Ready(it) }
                .onFailure { _uiState.value = TeamUiState.Error("Unable to load teams.") }
        }
    }

    suspend fun saveTeam(team: Team): Result<Team> = runCatching {
        val saved = if (team.id.isBlank()) repository.createTeam(team) else repository.updateTeam(team.id, team)
        _message.value = if (team.id.isBlank()) "Team created successfully." else "Team updated successfully."
        loadTeams()
        saved
    }.onFailure { _message.value = "Unable to save team. Please check your connection and try again." }

    fun deleteTeam(id: String) {
        viewModelScope.launch {
            runCatching { repository.deleteTeam(id) }
                .onSuccess {
                    _message.value = "Team deleted successfully."
                    loadTeams()
                }
                .onFailure { _message.value = "Unable to delete team. Players must be moved or unassigned first." }
        }
    }

    fun clearMessage() {
        _message.value = null
    }

    private fun createApiService(): ApiService {
        val json = Json { ignoreUnknownKeys = true }
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .addConverterFactory(json.asConverterFactory(MediaType.parse("application/json")!!))
            .build()
            .create(ApiService::class.java)
    }
}
