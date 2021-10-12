package com.uramnoil.awesome_minecraft_console

import io.grpc.Server
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

class AcrobatServer(
    port: UShort,
    mutableOnlinePlayersFlow: MutableSharedFlow<OnlinePlayers>,
    context: CoroutineContext
) : CoroutineScope by CoroutineScope(context + Job(context.job) + CoroutineName("LoyalWolfWeaverAndAcrobatServer")) {
    private val server: Server = NettyServerBuilder
        .forPort(port.toInt())
        .permitKeepAliveWithoutCalls(true)
        .keepAliveTime(1_000L, TimeUnit.MILLISECONDS)
        .keepAliveTimeout(20_000L, TimeUnit.MILLISECONDS)
        .permitKeepAliveTime(1_000L, TimeUnit.MILLISECONDS)
        .permitKeepAliveWithoutCalls(true)
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
                this@AcrobatServer.shutdown()
            }
        )
    }

    suspend fun joinUntilShutdown() {
        withContext(Dispatchers.Default) {
            server.awaitTermination()
        }
    }

    fun shutdown() {
        server.shutdown()
        coroutineContext.cancelChildren()
    }
}