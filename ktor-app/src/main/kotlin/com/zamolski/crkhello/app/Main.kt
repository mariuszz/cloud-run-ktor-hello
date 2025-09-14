package com.zamolski.crkhello.app

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory
import java.util.UUID
import kotlin.uuid.Uuid

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    val buildMode = System.getenv("BUILD_MODE") ?: "UNKNOWN"

    val logger = LoggerFactory.getLogger("cloud-run-ktor-hello")
    val projectId = System.getenv("PROJECT_ID") ?: "unknown"

    embeddedServer(Netty, port = port) {
        configureLogging(projectId)
        routing {
            get("/") {
                call.respondText("Hello from Cloud Run! [${buildMode}]")
            }
            get("/health") {
                logger.info("Health check requested")
                call.respondText("OK")
            }
            get("/error") {
                logger.error("Thrown exception", RuntimeException())
                call.respondText("ERROR")
            }
        }
    }.start(wait = true)
}

fun Application.configureLogging(projectId: String) {
    install(CallId) {
        retrieve {
            val header = it.request.headers["traceparent"]
            header?.split("-")?.getOrNull(1)
        }
        generate {
            UUID.randomUUID().toString()
        }
        verify { it.isNotBlank() }
    }
    install(CallLogging) {
        callIdMdc("trace_id")
        mdc("logging.googleapis.com/trace") { call ->
            call.callId?.let { "projects/$projectId/traces/$it" }
        }
    }

}