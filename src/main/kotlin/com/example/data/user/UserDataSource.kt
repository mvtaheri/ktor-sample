package com.example.data.user

import com.example.models.User

interface UserDataSource {
    suspend fun getUserByUserName(username: String): User?
    suspend fun insertUser(user: User): Boolean
}