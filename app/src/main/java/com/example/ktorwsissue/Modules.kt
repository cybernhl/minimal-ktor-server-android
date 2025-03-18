package com.example.ktorwsissue

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.websocket.WebSockets

fun Application.configureCommonPlugins() {
    installWebSockets()
    installCallLogging()
    configureRouting()
}

private fun Application.installWebSockets() {
    install(WebSockets) {
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
}

private fun Application.installCallLogging() {
    install(CallLogging)

}
