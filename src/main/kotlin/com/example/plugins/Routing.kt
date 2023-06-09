package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDate

fun Application.configureRouting() {
    routing {
        authenticate("auth-jwt") {
            get("/imionvacation") {
                val today = LocalDate.now()
                if (today.dayOfMonth % 2 == 0) {
                    call.respondText("Today you have a day off")
                } else {
                    call.respondText("Today you have to work")
                }
            }
        }
    }
}
