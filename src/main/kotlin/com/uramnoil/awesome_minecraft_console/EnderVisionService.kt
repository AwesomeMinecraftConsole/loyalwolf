package com.uramnoil.awesome_minecraft_console

import awesome_minecraft_console.endervision.EnderVisionGrpcKt
import awesome_minecraft_console.endervision.Loyalwolf
import com.google.protobuf.Empty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class EnderVisionService(
    private val lineFlow: Flow<Line>,
    private val mutableCommandFlow: MutableSharedFlow<Command>,
    private val notificationFlow: Flow<Notification>,
    private val mutableOperationFlow: MutableSharedFlow<Operations>,
    private val onlinePlayersFlow: Flow<OnlinePlayers>,
    context: CoroutineContext
) : EnderVisionGrpcKt.EnderVisionCoroutineImplBase(), CoroutineScope by CoroutineScope(context) {
    override fun console(requests: Flow<Loyalwolf.Command>): Flow<Loyalwolf.Line> {
        launch { requests.map { Command(it) }.collect { mutableCommandFlow.emit(it) } }
        return lineFlow.map { it.build() }
    }

    override fun management(requests: Flow<Loyalwolf.Operation>): Flow<Loyalwolf.Notification> {
        launch { requests.map { Operations(it) }.collect { mutableOperationFlow.emit(it) } }
        return notificationFlow.map { it.build() }
    }

    override fun onlinePlayers(request: Empty): Flow<Loyalwolf.OnlinePlayersResponse> {
        return onlinePlayersFlow.map { it.build() }
    }
}