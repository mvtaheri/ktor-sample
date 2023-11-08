package com.example.data

import com.example.data.user.UserDataSource
import com.example.models.User
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.mongodb.client.model.Filters.eq
import kotlinx.coroutines.flow.firstOrNull

class MongoUserDataSource(
    db: MongoDatabase
) : UserDataSource {
    private val users = db.getCollection<User>("users")
    override suspend fun getUserByUserName(username: String): User? {
        return users.find(eq("username", username)).firstOrNull()
    }

    override suspend fun insertUser(user: User): Boolean {
        val result = users.insertOne(user)
        val insertedId = result.insertedId?.asObjectId()?.value
        val re = insertedId ?: false
        if (insertedId != null) return true
        else return false
    }
}