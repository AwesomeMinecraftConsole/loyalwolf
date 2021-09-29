package com.uramnoil.awesome_minecraft_console

import io.grpc.Server
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.concurrent.TimeUnit
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
    private val server: Server = NettyServerBuilder
        .forPort(port.toInt())
        .permitKeepAliveWithoutCalls(true)
        .keepAliveTime(1000L, TimeUnit.MILLISECONDS)
        .keepAliveTimeout(200000, TimeUnit.MILLISECONDS)
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