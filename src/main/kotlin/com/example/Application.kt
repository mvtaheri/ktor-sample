package com.example

import com.example.dao.DAOFacade
import com.example.dao.DAOFacadeImpl
import com.example.dao.DatabaseFactory
import com.example.plugins.*
import com.example.security.hashing.SHA256HashingService
import com.example.security.token.JWTTokenService
import com.example.security.token.TokenConfig
import io.ktor.serialization.gson.gson
import io.ktor.server.application.*
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(ContentNegotiation) {
      gson()
    }
    val tokenService = JWTTokenService()
    val tokenConfig = TokenConfig(
        environment.config.property("jwt.issuer").getString(),
        environment.config.property("jwt.audience").getString(),
        356L * 1000L * 60L * 60L * 24L,
        System.getenv("JWT_SECRET")
    )
    log.info("Hello from module!")
    val hashingService = SHA256HashingService()
    DatabaseFactory.init()


    val daoDafacde: DAOFacade = DAOFacadeImpl()
    configureMonitoring()
    configureSecurity(tokenConfig)
    configureRouting(
        tokenConfig,
        hashingService,
        daoDafacde,
        tokenService
    )
}
