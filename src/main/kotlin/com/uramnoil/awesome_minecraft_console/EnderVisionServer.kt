package com.uramnoil.awesome_minecraft_console

import io.grpc.Server
import io.grpc.ServerBuilder
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlin.coroutines.CoroutineContext

class EnderVisionServer(
    port: UShort,
    lineFlow: Flow<Line>,
    mutableCommandFlow: MutableSharedFlow<Command>,
    notificationFlow: Flow<Notification>,
    mutableOperationFlow: MutableSharedFlow<Operations>,
    onlinePlayersFlow: Flow<OnlinePlayers>,
    context: CoroutineContext
) : CoroutineScope by CoroutineScope(context + CoroutineName("LoyalWolfEnderVisionServer")) {
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