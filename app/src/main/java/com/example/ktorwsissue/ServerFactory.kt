package com.example.ktorwsissue

import io.ktor.server.application.Application
import io.ktor.server.engine.ApplicationEngineFactory

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.engine.*

object ServerFactory {
    fun getServer(
        engineFactory: ApplicationEngineFactory<*, *> = Netty,
        port: Int,
        host: String = "0.0.0.0",
        watchPaths: kotlin.collections.List<kotlin.String> = emptyList(),
        modules: Application.() -> Unit
    ): EmbeddedServer<*, *> {
        return embeddedServer(engineFactory, port = port, host = host, watchPaths,module = modules)
    }
}