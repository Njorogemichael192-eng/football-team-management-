package com.football.teammanager.data

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("players")
    suspend fun getPlayers(): List<Player>

    @GET("players/{id}")
    suspend fun getPlayer(@Path("id") id: String): Player

    @POST("players")
    suspend fun createPlayer(@Body player: Player): Player

    @PUT("players/{id}")
    suspend fun updatePlayer(@Path("id") id: String, @Body player: Player): Player

    @DELETE("players/{id}")
    suspend fun deletePlayer(@Path("id") id: String)

    @GET("teams")
    suspend fun getTeams(): List<Team>

    @GET("teams/{id}")
    suspend fun getTeam(@Path("id") id: String): Team

    @POST("teams")
    suspend fun createTeam(@Body team: Team): Team

    @PUT("teams/{id}")
    suspend fun updateTeam(@Path("id") id: String, @Body team: Team): Team

    @DELETE("teams/{id}")
    suspend fun deleteTeam(@Path("id") id: String)
}
