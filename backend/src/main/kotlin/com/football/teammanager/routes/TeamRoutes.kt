package com.football.teammanager.routes

import com.football.teammanager.database.MongoDatabase
import com.football.teammanager.model.Team
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.teamRoutes(database: MongoDatabase) {
    route("/teams") {
        get {
            call.respond(database.findAllTeams())
        }

        get("/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val team = database.findTeamById(id) ?: return@get call.respond(HttpStatusCode.NotFound, "Team not found")
            call.respond(team)
        }

        post {
            val team = call.receive<Team>()
            val validationError = validateTeam(team)
            if (validationError != null) return@post call.respond(HttpStatusCode.BadRequest, validationError)
            if (database.teamNameExists(team.name)) return@post call.respond(HttpStatusCode.Conflict, "A team with this name already exists")
            call.respond(HttpStatusCode.Created, database.insertTeam(team))
        }

        put("/{id}") {
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            if (database.findTeamById(id) == null) return@put call.respond(HttpStatusCode.NotFound, "Team not found")
            val team = call.receive<Team>()
            val validationError = validateTeam(team)
            if (validationError != null) return@put call.respond(HttpStatusCode.BadRequest, validationError)
            val existingTeam = database.findTeamByName(team.name)
            if (existingTeam != null && existingTeam.id != id) return@put call.respond(HttpStatusCode.Conflict, "A team with this name already exists")
            val updatedTeam = database.updateTeam(id, team)
                ?: return@put call.respond(HttpStatusCode.NotFound, "Team not found")
            call.respond(updatedTeam)
        }

        delete("/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (database.hasPlayersForTeam(id)) {
                return@delete call.respond(HttpStatusCode.Conflict, "Players must be moved or unassigned before deleting this team")
            }
            if (!database.deleteTeam(id)) return@delete call.respond(HttpStatusCode.NotFound, "Team not found")
            call.respond(HttpStatusCode.NoContent)
        }
    }
}

private fun validateTeam(team: Team): String? = when {
    team.name.trim().isEmpty() -> "Team name is required"
    team.coach.name.trim().isEmpty() -> "Coach name is required"
    team.coach.age !in 18..100 -> "Coach age must be between 18 and 100"
    team.coach.gamesManaged < 0 -> "Games managed must be zero or greater"
    team.coach.gamesWon < 0 -> "Games won must be zero or greater"
    team.coach.gamesDrawn < 0 -> "Games drawn must be zero or greater"
    team.coach.gamesLost < 0 -> "Games lost must be zero or greater"
    team.coach.gamesWon + team.coach.gamesDrawn + team.coach.gamesLost > team.coach.gamesManaged -> "Games won, drawn, and lost cannot exceed games managed"
    else -> null
}
