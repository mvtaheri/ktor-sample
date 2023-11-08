package com.example.dao

import com.example.dao.DatabaseFactory.dbQuery
import com.example.models.*
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerializationException
import org.jetbrains.exposed.sql.*
import kotlin.math.log


class DAOFacadeImpl : DAOFacade {

    override suspend fun addNewUser(username: String, password: String, salt: String): User? =
        dbQuery {
            val insertStatement = Users.insert {
                it[Users.username] = username
                it[Users.password] = password
                it[Users.salt] = salt
            }
            insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToArticle)
        }

    override suspend fun getAllUsers(): List<User> = dbQuery {
        Users.selectAll().map(::resultRowToArticle)
    }

    override suspend fun userByUsername(username: String): User? = dbQuery {
        Users.select { Users.username like ("%${username}%") }
            .map(::resultRowToArticle)
            .singleOrNull()
    }

    override suspend fun users(id: Int): User? = dbQuery {
        Users
            .select { Users.id eq id }
            .map(::resultRowToArticle)
            .singleOrNull()
    }


    private fun resultRowToArticle(row: ResultRow) = User(
        id = row[Users.id],
        username = row[Users.username],
        password = row[Users.password],
        salt = row[Users.salt]
    )
}