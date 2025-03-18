package com.example.ktorwsissue

import android.os.Build
import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.FrameType
import io.ktor.websocket.close
import io.ktor.websocket.readBytes
import io.ktor.websocket.send
import java.nio.charset.Charset
import java.util.logging.Logger

fun Application.configureRouting() {
    val logger = Logger.getLogger("KtorServer")
    routing {
        get("/") {
            call.respondText("All good here in ${Build.MODEL}", ContentType.Text.Plain)
        }
        webSocketRoutes()
    }
}

private fun Route.webSocketRoutes() {
    val logger = Logger.getLogger("KtorServer")
    webSocket("/ws") {
        logger.info("Sending message to client...")
        send("foo")

        val receivedMessage = incoming.receive()
        val messageFormatted = if (receivedMessage.frameType == FrameType.TEXT) {
            receivedMessage.readBytes().toString(Charset.defaultCharset())
        } else {
            "<non-text frame>"
        }
        logger.info("Got message from client: $messageFormatted")

        logger.info("Closing connection...")
        close()
    }
}