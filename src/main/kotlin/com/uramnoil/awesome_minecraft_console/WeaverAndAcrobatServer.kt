package com.uramnoil.awesome_minecraft_console

import io.grpc.Server
import io.grpc.ServerBuilder
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlin.coroutines.CoroutineContext

class WeaverAndAcrobatServer(
    port: Short,
    mutableLineFlow: MutableSharedFlow<Line>,
    commandFlow: Flow<Command>,
    mutableNotificationFlow: MutableSharedFlow<Notification>,
    operationFlow: Flow<Operations>,
    mutableOnlinePlayersFlow: MutableSharedFlow<OnlinePlayers>,
    context: CoroutineContext) :
    CoroutineScope by CoroutineScope(context)
{
    private val server: Server = ServerBuilder
        .forPort(port.toInt())
        .addService(
            WeaverService(
                mutableLineFlow,
                commandFlow,
                mutableNotificationFlow,
                operationFlow,
                coroutineContext
            )
        )
        .addService(
            AcrobatService(
                mutableOnlinePlayersFlow,
                coroutineContext
            )
        )
        .build()

    fun start() {
        server.start()
        Runtime.getRuntime().addShutdownHook(
            Thread {
                this@WeaverAndAcrobatServer.stop()
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