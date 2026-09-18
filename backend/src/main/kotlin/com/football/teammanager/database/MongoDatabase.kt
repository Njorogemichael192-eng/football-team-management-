package com.football.teammanager.database

import com.football.teammanager.model.Player
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import org.bson.Document
import org.bson.types.ObjectId

class MongoDatabase(mongodbUri: String) : AutoCloseable {
    private val client: MongoClient = MongoClients.create(mongodbUri)
    private val collection: MongoCollection<Document> = client
        .getDatabase("football_team")
        .getCollection("players")

    fun findAll(): List<Player> = collection.find().map(::toPlayer).toList()

    fun findById(id: String): Player? = objectId(id)?.let { collection.find(Filters.eq("_id", it)).first()?.let(::toPlayer) }

    fun insert(player: Player): Player {
        val document = toDocument(player)
        collection.insertOne(document)
        return toPlayer(document)
    }

    fun update(id: String, player: Player): Player? {
        val objectId = objectId(id) ?: return null
        val result = collection.findOneAndUpdate(
            Filters.eq("_id", objectId),
            Updates.combine(
                Updates.set("name", player.name), Updates.set("age", player.age),
                Updates.set("position", player.position), Updates.set("jerseyNumber", player.jerseyNumber),
                Updates.set("gamesPlayed", player.gamesPlayed), Updates.set("gamesSubstituted", player.gamesSubstituted),
                Updates.set("goalsScored", player.goalsScored), Updates.set("assists", player.assists)
            )
        )
        return if (result == null) null else findById(id)
    }

    fun delete(id: String): Boolean {
        val objectId = objectId(id) ?: return false
        return collection.deleteOne(Filters.eq("_id", objectId)).deletedCount == 1L
    }

    override fun close() = client.close()

    private fun objectId(id: String): ObjectId? = runCatching { ObjectId(id) }.getOrNull()

    private fun toDocument(player: Player) = Document()
        .append("_id", if (player.id.isBlank()) ObjectId() else ObjectId(player.id))
        .append("name", player.name).append("age", player.age).append("position", player.position)
        .append("jerseyNumber", player.jerseyNumber).append("gamesPlayed", player.gamesPlayed)
        .append("gamesSubstituted", player.gamesSubstituted).append("goalsScored", player.goalsScored)
        .append("assists", player.assists)

    private fun toPlayer(document: Document) = Player(
        id = document.getObjectId("_id").toHexString(), name = document.getString("name"),
        age = document.getInteger("age"), position = document.getString("position"),
        jerseyNumber = document.getInteger("jerseyNumber"), gamesPlayed = document.getInteger("gamesPlayed"),
        gamesSubstituted = document.getInteger("gamesSubstituted"), goalsScored = document.getInteger("goalsScored"),
        assists = document.getInteger("assists")
    )
}
