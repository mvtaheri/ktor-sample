package com.example.security.hashing

interface HashingService {
    fun generateSlatedHash(value: String, saltLength: Int = 32): SaltedHash
    fun verify(value: String, saltedHash: SaltedHash): Boolean
}