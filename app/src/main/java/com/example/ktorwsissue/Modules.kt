package com.example.ktorwsissue

import android.os.Build
import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.CallLogging

import io.ktor.server.response.respondText
import java.nio.charset.Charset
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.FrameType
import io.ktor.websocket.close
import io.ktor.websocket.readBytes
import io.ktor.websocket.send
import java.time.Duration
import kotlin.time.Duration.Companion.seconds

fun Application.configureCommonPlugins() {
    installWebSockets()
    installCallLogging()
    configureRouting()
}

private fun Application.installWebSockets() {
    install(WebSockets) {
        // 可添加進階 WebSocket 配置

        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
}

private fun Application.installCallLogging() {
    install(CallLogging)

}


