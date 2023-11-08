package com.example.dao

import com.example.models.*

interface DAOFacade {
    suspend fun users(id: Int): User?
    suspend fun userByUsername(username:String):User?
    suspend fun getAllUsers():List<User>
    suspend fun addNewUser(username: String, password: String,salt:String): User?
}