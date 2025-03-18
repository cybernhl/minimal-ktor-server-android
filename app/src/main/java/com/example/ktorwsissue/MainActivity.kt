package com.example.ktorwsissue

import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.ktor.http.ContentType
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.response.respondText
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.net.NetworkInterface
import java.nio.charset.Charset
import java.util.logging.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val coroutineContext = Dispatchers.IO

    private val logger = Logger.getLogger("KtorServer")

    private val server by lazy {
        embeddedServer(Netty, 13276, watchPaths = emptyList()) {
            install(WebSockets)
            install(CallLogging)

            routing {
                get("/") {
                    call.respondText("All good here in ${Build.MODEL}", ContentType.Text.Plain)
                }

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
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CoroutineScope(coroutineContext).launch {
            logger.info("Starting server...")
            server.start(wait = true)
        }

        findViewById<TextView>(R.id.serverStatusText).text = getString(R.string.serverStartedMessage)

        val localIpAddress = getIpAddressInLocalNetwork()
        if (localIpAddress != null) {
            findViewById<TextView>(R.id.ipAddressText).text =
                getString(R.string.localIpAddressMessage, localIpAddress)
        }
    }

    override fun onDestroy() {
        logger.info("Stopping server")
        server.stop(1_000, 2_000)

        super.onDestroy()
    }

    private fun getIpAddressInLocalNetwork(): String? {
        val networkInterfaces = NetworkInterface.getNetworkInterfaces().iterator().asSequence()
        val localAddresses = networkInterfaces.flatMap {
            it.inetAddresses.asSequence()
                .filter { inetAddress ->
                    inetAddress.isSiteLocalAddress && !inetAddress.hostAddress.contains(":") &&
                        inetAddress.hostAddress != "127.0.0.1"
                }
                .map { inetAddress -> inetAddress.hostAddress }
        }
        return localAddresses.firstOrNull()
    }
}