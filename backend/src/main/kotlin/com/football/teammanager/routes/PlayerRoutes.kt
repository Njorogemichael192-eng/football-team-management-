package com.football.teammanager.routes

import com.football.teammanager.database.MongoDatabase
import com.football.teammanager.model.Player
import com.football.teammanager.model.allowedPositions
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.playerRoutes(database: MongoDatabase) {
    route("/players") {
        get { call.respond(database.findAll()) }
        get("/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val player = database.findById(id) ?: return@get call.respond(HttpStatusCode.NotFound, "Player not found")
            call.respond(player)
        }
        post {
            val player = call.receive<Player>()
            val validationError = validate(player)
            if (validationError != null) return@post call.respond(HttpStatusCode.BadRequest, validationError)
            call.respond(HttpStatusCode.Created, database.insert(player))
        }
        put("/{id}") {
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            if (database.findById(id) == null) return@put call.respond(HttpStatusCode.NotFound, "Player not found")
            val player = call.receive<Player>()
            val validationError = validate(player)
            if (validationError != null) return@put call.respond(HttpStatusCode.BadRequest, validationError)
            val updatedPlayer = database.update(id, player)
                ?: return@put call.respond(HttpStatusCode.NotFound, "Player not found")
            call.respond(updatedPlayer)
        }
        delete("/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (!database.delete(id)) return@delete call.respond(HttpStatusCode.NotFound, "Player not found")
            call.respond(HttpStatusCode.NoContent)
        }
    }
}

private fun validate(player: Player): String? = when {
    player.name.isBlank() -> "Name is required"
    player.age !in 16..60 -> "Age must be between 16 and 60"
    player.position !in allowedPositions -> "Position is invalid"
    player.jerseyNumber !in 1..99 -> "Jersey number must be between 1 and 99"
    listOf(player.gamesPlayed, player.gamesSubstituted, player.goalsScored, player.assists).any { it < 0 } -> "Statistics must be zero or greater"
    else -> null
}
