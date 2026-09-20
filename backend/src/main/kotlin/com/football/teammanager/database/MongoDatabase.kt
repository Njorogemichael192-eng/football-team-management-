package com.football.teammanager.database

import com.football.teammanager.model.Coach
import com.football.teammanager.model.Player
import com.football.teammanager.model.Team
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import org.bson.Document
import org.bson.types.ObjectId

class MongoDatabase(mongodbUri: String) : AutoCloseable {
    private val client: MongoClient = MongoClients.create(mongodbUri)
    private val database = client.getDatabase("football_team")
    private val playersCollection: MongoCollection<Document> = database.getCollection("players")
    private val teamsCollection: MongoCollection<Document> = database.getCollection("teams")

    fun findAll(): List<Player> = playersCollection.find().map(::toPlayer).toList()

    fun findById(id: String): Player? = objectId(id)?.let { playersCollection.find(Filters.eq("_id", it)).first()?.let(::toPlayer) }

    fun insert(player: Player): Player {
        val document = toDocument(player)
        playersCollection.insertOne(document)
        return toPlayer(document)
    }

    fun update(id: String, player: Player): Player? {
        val objectId = objectId(id) ?: return null
        val result = playersCollection.findOneAndUpdate(
            Filters.eq("_id", objectId),
            Updates.combine(
                Updates.set("name", player.name), Updates.set("age", player.age),
                Updates.set("position", player.position), Updates.set("jerseyNumber", player.jerseyNumber),
                Updates.set("gamesPlayed", player.gamesPlayed), Updates.set("gamesSubstituted", player.gamesSubstituted),
                Updates.set("goalsScored", player.goalsScored), Updates.set("assists", player.assists),
                Updates.set("teamId", player.teamId)
            )
        )
        return if (result == null) null else findById(id)
    }

    fun delete(id: String): Boolean {
        val objectId = objectId(id) ?: return false
        return playersCollection.deleteOne(Filters.eq("_id", objectId)).deletedCount == 1L
    }

    fun findAllTeams(): List<Team> = teamsCollection.find().map(::toTeam).toList()

    fun findTeamById(id: String): Team? = objectId(id)?.let { teamsCollection.find(Filters.eq("_id", it)).first()?.let(::toTeam) }

    fun findTeamByName(name: String): Team? = teamsCollection.find(Filters.eq("name", name.trim())).first()?.let(::toTeam)

    fun teamNameExists(name: String): Boolean = teamsCollection.find(Filters.eq("name", name.trim())).first() != null

    fun hasPlayersForTeam(teamId: String): Boolean = playersCollection.find(Filters.eq("teamId", teamId)).first() != null

    fun insertTeam(team: Team): Team {
        val document = toTeamDocument(team)
        teamsCollection.insertOne(document)
        return toTeam(document)
    }

    fun updateTeam(id: String, team: Team): Team? {
        val objectId = objectId(id) ?: return null
        val result = teamsCollection.findOneAndUpdate(
            Filters.eq("_id", objectId),
            Updates.combine(
                Updates.set("name", team.name.trim()),
                Updates.set("coach", toCoachDocument(team.coach)),
                Updates.set("createdAt", team.createdAt)
            )
        )
        return if (result == null) null else findTeamById(id)
    }

    fun deleteTeam(id: String): Boolean {
        val objectId = objectId(id) ?: return false
        return teamsCollection.deleteOne(Filters.eq("_id", objectId)).deletedCount == 1L
    }

    override fun close() = client.close()

    private fun objectId(id: String): ObjectId? = runCatching { ObjectId(id) }.getOrNull()

    private fun toDocument(player: Player) = Document()
        .append("_id", if (player.id.isBlank()) ObjectId() else ObjectId(player.id))
        .append("name", player.name).append("age", player.age).append("position", player.position)
        .append("jerseyNumber", player.jerseyNumber).append("gamesPlayed", player.gamesPlayed)
        .append("gamesSubstituted", player.gamesSubstituted).append("goalsScored", player.goalsScored)
        .append("assists", player.assists).append("teamId", player.teamId)

    private fun toPlayer(document: Document) = Player(
        id = document.getObjectId("_id").toHexString(), name = document.getString("name"),
        age = document.getInteger("age"), position = document.getString("position"),
        jerseyNumber = document.getInteger("jerseyNumber"), gamesPlayed = document.getInteger("gamesPlayed"),
        gamesSubstituted = document.getInteger("gamesSubstituted"), goalsScored = document.getInteger("goalsScored"),
        assists = document.getInteger("assists"), teamId = document.getString("teamId")
    )

    private fun toTeamDocument(team: Team) = Document()
        .append("_id", if (team.id.isBlank()) ObjectId() else ObjectId(team.id))
        .append("name", team.name.trim())
        .append("coach", toCoachDocument(team.coach))
        .append("createdAt", team.createdAt.ifBlank { java.time.Instant.now().toString() })

    private fun toCoachDocument(coach: Coach): Document = Document()
        .append("name", coach.name)
        .append("age", coach.age)
        .append("phone", coach.phone)
        .append("gamesManaged", coach.gamesManaged)
        .append("gamesWon", coach.gamesWon)
        .append("gamesDrawn", coach.gamesDrawn)
        .append("gamesLost", coach.gamesLost)

    private fun toTeam(document: Document): Team {
        val coachDocument = document.get("coach", Document::class.java)
        val coach = Coach(
            name = coachDocument.getString("name") ?: "",
            age = coachDocument.getInteger("age") ?: 0,
            phone = coachDocument.getString("phone"),
            gamesManaged = coachDocument.getInteger("gamesManaged") ?: 0,
            gamesWon = coachDocument.getInteger("gamesWon") ?: 0,
            gamesDrawn = coachDocument.getInteger("gamesDrawn") ?: 0,
            gamesLost = coachDocument.getInteger("gamesLost") ?: 0
        )
        return Team(
            id = document.getObjectId("_id").toHexString(),
            name = document.getString("name") ?: "",
            coach = coach,
            createdAt = document.getString("createdAt") ?: ""
        )
    }
}
