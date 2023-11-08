package com.example.data.response

import com.example.models.User
import kotlinx.serialization.Serializable

@Serializable
data class Response(
    val user:User?
)