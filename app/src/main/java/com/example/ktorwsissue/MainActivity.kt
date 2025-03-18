package com.example.ktorwsissue

import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.ktor.http.ContentType
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.net.NetworkInterface
import java.util.logging.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val coroutineContext = Dispatchers.IO

    private val logger = Logger.getLogger("KtorServer")

    private val server = ServerFactory.getServer(Netty, port = 13276, watchPaths = emptyList(),modules = Application::configureCommonPlugins)

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