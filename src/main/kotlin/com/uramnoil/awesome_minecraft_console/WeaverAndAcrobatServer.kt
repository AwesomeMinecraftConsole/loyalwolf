package com.uramnoil.awesome_minecraft_console

import io.grpc.Server
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

class WeaverAndAcrobatServer(
    port: UShort,
    mutableLineFlow: MutableSharedFlow<Line>,
    commandFlow: Flow<Command>,
    mutableNotificationFlow: MutableSharedFlow<Notification>,
    operationFlow: Flow<Operations>,
    mutableOnlinePlayersFlow: MutableSharedFlow<OnlinePlayers>,
    context: CoroutineContext
) : CoroutineScope by CoroutineScope(context + CoroutineName("LoyalWolfWeaverAndAcrobatServer"))
{
    private val server: Server = NettyServerBuilder
        .forPort(port.toInt())
        .permitKeepAliveWithoutCalls(true)
        .keepAliveTime(1000L, TimeUnit.MILLISECONDS)
        .keepAliveTimeout(200000, TimeUnit.MILLISECONDS)
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