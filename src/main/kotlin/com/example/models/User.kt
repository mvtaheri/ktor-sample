package com.example.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.jetbrains.exposed.sql.*

data class User(
    var username: String,
    var password: String,
    var salt: String,
    var id: Int
)

object Users : Table() {
    var id = integer("id").autoIncrement()
    var username = varchar("username", 128)
    var password = varchar("password", 1024)
    var salt = varchar("salt", 128)
    override var primaryKey = PrimaryKey(id)
}