package com.uramnoil.awesome_minecraft_console

import io.grpc.Server
import io.grpc.ServerBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class EnderVisionServer(
    port: Short,
    lineFlow: Flow<Line>,
    mutableCommandFlow: MutableSharedFlow<Command>,
    notificationFlow: Flow<Notification>,
    mutableOperationFlow: MutableSharedFlow<Operations>,
    onlinePlayersFlow: Flow<OnlinePlayers>,
    context: CoroutineContext
) : CoroutineScope by CoroutineScope(context) {
    private val server: Server = ServerBuilder
        .forPort(port.toInt())
        .addService(
            EnderVisionService(
                lineFlow,
                mutableCommandFlow,
                notificationFlow,
                mutableOperationFlow,
                onlinePlayersFlow,
                coroutineContext
            )
        )
        .build()

    fun start() {
        server.start()
        Runtime.getRuntime().addShutdownHook(
            Thread {
                this@EnderVisionServer.stop()
            }
        )
    }

    fun stop() {
        server.shutdown()
        coroutineContext.cancel()
    }

    suspend fun joinUntilShutdown() {
        withContext(Dispatchers.Default) {
            server.awaitTermination()
        }
    }
}