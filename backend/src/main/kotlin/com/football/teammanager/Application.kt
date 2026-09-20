package com.football.teammanager

import com.football.teammanager.database.MongoDatabase
import com.football.teammanager.routes.playerRoutes
import com.football.teammanager.routes.teamRoutes
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.routing.routing
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(Netty, port = System.getenv("PORT")?.toIntOrNull() ?: 8080, host = "0.0.0.0", module = Application::module).start(wait = true)
}

fun Application.module() {
    val mongodbUri = System.getenv("MONGODB_URI")
        ?: error("MONGODB_URI environment variable is required")
    val database = MongoDatabase(mongodbUri)

    environment.monitor.subscribe(io.ktor.server.application.ApplicationStopped) { database.close() }
    install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
    install(CORS) {
        anyHost()
        allowHeader("Content-Type")
        allowHeader("Accept")
        allowMethod(io.ktor.http.HttpMethod.Get)
        allowMethod(io.ktor.http.HttpMethod.Post)
        allowMethod(io.ktor.http.HttpMethod.Put)
        allowMethod(io.ktor.http.HttpMethod.Delete)
    }
    routing {
        playerRoutes(database)
        teamRoutes(database)
    }
}
