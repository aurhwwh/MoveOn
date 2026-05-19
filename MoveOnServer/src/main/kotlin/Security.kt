package MoveOn

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.security.MessageDigest
import java.util.*

fun Application.configureSecurity() {

    val secret = "secret"
    val issuer = "MoveOn"

    install(Authentication) {
        jwt("auth-jwt") {
            verifier(
                JWT.require(Algorithm.HMAC256(secret))
                    .withIssuer(issuer)
                    .build()
            )

            validate { credential ->
                val userId = credential.payload.getClaim("userId").asInt()
                val type = credential.payload.getClaim("type").asString()
                if (userId != null && type == "access") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}

fun generateAccessToken(userId: Int): String {

    val secret = "secret"
    val issuer = "MoveOn"

    return JWT.create()
        .withIssuer(issuer)
        .withClaim("userId", userId)
        .withClaim("type", "access")
        .withExpiresAt(Date(System.currentTimeMillis() + 3600000))
        .sign(Algorithm.HMAC256(secret))
}

fun generateRefreshToken(userId: Int): String {
    val secret = "secret"
    val issuer = "MoveOn"

    return JWT.create()
        .withIssuer(issuer)
        .withClaim("userId", userId)
        .withClaim("type", "refresh")
        .withExpiresAt(Date(System.currentTimeMillis() + 30L*24*3600000))
        .sign(Algorithm.HMAC256(secret))
}

fun getUserIdFromJWT(jwt: String): Int? {
    return try {
        val verifier = JWT.require(Algorithm.HMAC256("secret"))
            .withIssuer("MoveOn")
            .build()
        val decoded = verifier.verify(jwt)
        decoded.getClaim("userId").asInt()
    } catch (e: Exception) {
        null
    }
}

fun isRefreshToken(jwt: String): Boolean {
    return try {
        val verifier = JWT.require(Algorithm.HMAC256("secret"))
            .withIssuer("MoveOn")
            .build()

        val decoded = verifier.verify(jwt)

        val type = decoded.getClaim("type").asString()
        type == "refresh"
    } catch (e: Exception) {
        false
    }
}

fun hashRefreshToken(token: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val hashBytes = digest.digest(token.toByteArray())
    return Base64.getEncoder().encodeToString(hashBytes)
}

fun verifyRefreshToken(token: String, hash: String): Boolean {
    val newHash = hashRefreshToken(token)
    return newHash == hash
}