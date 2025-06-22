package com.zamolski.crkhello

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080

    embeddedServer(Netty, port = port) {
        routing {
            get("/") {
                call.respondText("Hello from Cloud Run!")
            }
        }
    }.start(wait = true)
}