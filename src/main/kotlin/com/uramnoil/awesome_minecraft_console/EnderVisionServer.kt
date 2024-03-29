package com.uramnoil.awesome_minecraft_console

import io.grpc.Server
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

/**
 * KeepAliveTime: 1,000ms
 * KeepAliveTimeout: 20,000ms
 */
class EnderVisionServer(
    port: UShort,
    lineFlow: Flow<Line>,
    mutableCommandFlow: MutableSharedFlow<Command>,
    notificationFlow: Flow<Notification>,
    mutableOperationFlow: MutableSharedFlow<Operations>,
    onlinePlayersFlow: Flow<OnlinePlayers>,
    context: CoroutineContext
) : CoroutineScope by CoroutineScope(context + Job(context.job) + CoroutineName("LoyalWolfEnderVisionServer")) {
    private val server: Server = NettyServerBuilder
        .forPort(port.toInt())
        .keepAliveTime(1_000, TimeUnit.MILLISECONDS)
        .keepAliveTimeout(20_000, TimeUnit.MILLISECONDS)
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
        .permitKeepAliveTime(1_000L, TimeUnit.MILLISECONDS)
        .permitKeepAliveWithoutCalls(true)
        .build()

    fun start() {
        server.start()
        Runtime.getRuntime().addShutdownHook(
            Thread {
                this@EnderVisionServer.shutdown()
            }
        )
    }

    fun shutdown() {
        server.shutdown()
        coroutineContext.cancelChildren()
    }

    suspend fun joinUntilShutdown() {
        withContext(Dispatchers.Default) {
            server.awaitTermination()
        }
    }
}