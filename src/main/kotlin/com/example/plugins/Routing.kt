package com.example.plugins

import com.example.authenticate
import com.example.dao.DAOFacade
import com.example.getSecretInfo
import com.example.getUsers
import com.example.home
import com.example.security.hashing.HashingService
import com.example.security.token.TokenConfig
import com.example.security.token.TokenService
import com.example.signIn
import com.example.signup
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Application.configureRouting(
    tokenConfig: TokenConfig,
    hashingService: HashingService,
    daoDafacde: DAOFacade,
    tokenService: TokenService
) {
    routing {
        home(daoDafacde)
        getUsers(daoDafacde)
        signIn(
            hashingService,
            daoDafacde,
            tokenService,
            tokenConfig
        )
        signup(hashingService,daoDafacde)
        authenticate()
        getSecretInfo()
    }

}
