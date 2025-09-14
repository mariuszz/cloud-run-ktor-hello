package com.zamolski.crkhello.app

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    val buildMode = System.getenv("BUILD_MODE") ?: "UNKNOWN"

    val logger = LoggerFactory.getLogger("cloud-run-ktor-hello")
    val projectId = System.getenv("PROJECT_ID") ?: "unknown"

    embeddedServer(Netty, port = port) {
        configureLogging(projectId)
        routing {
            get("/") {
                call.respondText("Hello from Cloud Run! [${buildMode}], project id: $projectId")
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

    install(CallLogging) {

        mdc("logging.googleapis.com/trace") { call ->
            val traceParent = call.request.headers["traceparent"]
            val traceId = traceParent?.split("-")?.getOrNull(1)
            traceId?.let { "projects/$projectId/traces/$it" }
        }

        mdc("logging.googleapis.com/spanId") { call ->
            val traceParent = call.request.headers["traceparent"]
            traceParent?.split("-")?.getOrNull(2)
        }

        mdc("logging.googleapis.com/trace_sampled") { call ->
            val traceParent = call.request.headers["traceparent"]
            val sampled = traceParent?.split("-")?.getOrNull(3)
            if (sampled == "01") "true" else "false"
        }
    }

}