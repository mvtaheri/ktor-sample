package com.example.security.hashing

import com.example.models.Users
import com.example.models.Users.autoIncrement
import org.jetbrains.exposed.sql.*

data class SaltedHash(
    var hash: String,
    var salt: String
)

object SaltedHashs : Table() {
    var hash = SaltedHashs.varchar("hash", 128)
    var salt = SaltedHashs.varchar("salt", 256)
}