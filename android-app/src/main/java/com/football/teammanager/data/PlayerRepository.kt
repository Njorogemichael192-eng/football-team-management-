package com.football.teammanager.data

class PlayerRepository(private val apiService: ApiService) {
    suspend fun getPlayers() = apiService.getPlayers()
    suspend fun getPlayer(id: String) = apiService.getPlayer(id)
    suspend fun createPlayer(player: Player) = apiService.createPlayer(player)
    suspend fun updatePlayer(id: String, player: Player) = apiService.updatePlayer(id, player)
    suspend fun deletePlayer(id: String) = apiService.deletePlayer(id)
}
