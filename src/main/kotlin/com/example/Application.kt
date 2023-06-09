package com.example

import com.auth0.jwk.UrlJwkProvider
import com.example.plugins.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import java.net.URL
import java.util.*

fun main() {
    embeddedServer(Netty, port = 4040, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val provider = "http://localhost:8080/realms/lunarealm/protocol/openid-connect/certs"
    val issuer = "http://localhost:8080/realms/lunarealm"
    val lunaRealm = "lunarealm"
    val audience = "luna-vacation-bot"
    val authorizedParty = "luna-vacation-bot"

    val keycloakProvider = UrlJwkProvider(URL(provider))

    install(Authentication) {
        jwt("auth-jwt") {
            // Specifies a JWT realm to be passed in WWW-Authenticate header
            realm = lunaRealm
            // Provides a JWTVerifier used to verify a token format and signature.
            verifier(keycloakProvider, issuer) {
                withAudience(audience)
                withClaim("azp", authorizedParty)
            }
            // Specifies what to send back if JWT authentication fails.
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
            // Allows you to perform additional validations on the JWT payload.
            validate { credential ->
                validateCredential(credential, issuer)
            }
        }
    }

    configureRouting()
}

fun validateCredential(credential: JWTCredential, issuer: String): JWTPrincipal? {
    if (credential.expiresAt?.after(Date()) == true
    ) {
        return JWTPrincipal(credential.payload)
    }

    return null
}
