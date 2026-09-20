package com.football.teammanager.data

class TeamRepository(private val apiService: ApiService) {
    suspend fun getTeams() = apiService.getTeams()
    suspend fun getTeam(id: String) = apiService.getTeam(id)
    suspend fun createTeam(team: Team) = apiService.createTeam(team)
    suspend fun updateTeam(id: String, team: Team) = apiService.updateTeam(id, team)
    suspend fun deleteTeam(id: String) = apiService.deleteTeam(id)
}
