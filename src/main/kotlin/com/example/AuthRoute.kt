package com.example

//import ch.qos.logback.classic.Logger
import com.example.dao.DAOFacade
import com.example.dao.DAOFacadeImpl
import com.example.data.MongoUserDataSource
import com.example.data.request.AuthRequest
import com.example.data.response.Response
import com.example.data.response.TokenResponse
import com.example.models.User
import com.example.security.hashing.HashingService
import com.example.security.hashing.SHA256HashingService
import com.example.security.hashing.SaltedHash
import com.example.security.token.TokenClaim
import com.example.security.token.TokenConfig
import com.example.security.token.TokenService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.application.receiveType
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receiveOrNull
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.util.getOrFail
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.math.log
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

fun Route.home(daoDafacde: DAOFacade) {
    get("/") {
        val users = withContext(Dispatchers.IO) { daoDafacde.getAllUsers() }
        call.respond(users)
    }
}

fun Route.getUsers(
    daoDafacde: DAOFacade
) {
    get("getuser/{username}") {
        val username = call.parameters.getOrFail<String>("username").toString()
        call.application.environment.log.info("getpathusername:${username}")
        var user2: User = withContext(Dispatchers.IO) {
            daoDafacde.userByUsername(username)!!
        }
        call.application.environment.log.info("getpathusername:${user2}")
        call.respond(user2)
        var user =
            DAOFacadeImpl().apply {
                runBlocking {
                    users(15)
                }
            }
    }
}

fun Route.signup(
    hashingService: HashingService,
    daoDafacde: DAOFacade
) {
    post("signup") {
        call.application.environment.log.info("signup path run:")
        val request = call.receiveOrNull<AuthRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        val areFieldBlank = request.username.isBlank() || request.password.isBlank()
        val isPwtooShort = request.password.length < 0
        if (areFieldBlank || isPwtooShort) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }
        val saltedHash = hashingService.generateSlatedHash(request.password)
        val wasAcknowLedged =
            withContext(Dispatchers.IO) {
            daoDafacde.addNewUser(
                request.username,
                request.password,
                saltedHash.salt
            )
        }
        call.application.environment.log.info("was acknowledged:${wasAcknowLedged}")
        if (wasAcknowLedged == null) {
            call.respond(HttpStatusCode.Conflict)
        }
        call.respond(HttpStatusCode.OK)
    }
}

fun Route.signIn(
    hashingService: HashingService,
    daoDafacde: DAOFacade,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    post("signIn") {
        val request = call.receiveOrNull<AuthRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        val user = withContext(Dispatchers.IO) { daoDafacde.userByUsername(request.username) }
        if (user == null) {
            call.respond(HttpStatusCode.Conflict, "Incorect username")
            return@post
        }
        val isValidPassword = hashingService.verify(
            request.password,
            SaltedHash(
                user.password.toString(),
                user.salt.toString()
            )
        )
        if (!isValidPassword) {
            call.respond(HttpStatusCode.Conflict, "Incoreect username or password")
            return@post
        }
        val token = tokenService.generate(
            tokenConfig,
            TokenClaim(
                "userId",
                user.id.toString()
            )
        )
        call.respond(HttpStatusCode.OK, TokenResponse(token))
    }
}

fun Route.authenticate() {
    authenticate {
        get("authenticate") {
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.getSecretInfo() {
    authenticate {
        get("secret") {
            val principle = call.principal<JWTPrincipal>()
            val userId = principle?.getClaim("userId", String::class)
            call.respond(HttpStatusCode.OK, "userId is ${userId}")
        }
    }

}
