package com.uramnoil.awesome_minecraft_console

import io.grpc.Server
import io.grpc.ServerBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlin.coroutines.CoroutineContext

class WeaverAndAcrobatServer(
    port: Short,
    mutableLineFlow: MutableSharedFlow<Line>,
    commandFlow: Flow<Command>,
    mutableNotificationFlow: MutableSharedFlow<Notification>,
    operationFlow: Flow<Operations>,
    context: CoroutineContext) :
    CoroutineScope by CoroutineScope(context)
{
    val server: Server = ServerBuilder
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
            AcrobatServ
        )
        .build()
}