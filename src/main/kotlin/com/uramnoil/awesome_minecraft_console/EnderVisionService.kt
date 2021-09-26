package com.uramnoil.awesome_minecraft_console

import awesome_minecraft_console.endervision.EnderVisionGrpcKt
import awesome_minecraft_console.endervision.Endervision
import awesome_minecraft_console.weaver.WeaverOuterClass
import com.google.protobuf.Empty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


private class EnderVisionService(
    private val lineFlow: Flow<Line>,
    private val mutableCommandFlow: MutableSharedFlow<Command>,
    private val notificationFlow: Flow<Notification>,
    private val mutableOperationFlow: MutableSharedFlow<Operations>,
    private val onlinePlayersFlow: MutableSharedFlow<OnlinePlayers>,
    private val scope: CoroutineScope
) : EnderVisionGrpcKt.EnderVisionCoroutineImplBase() {
    override fun console(requests: Flow<WeaverOuterClass.Command>): Flow<WeaverOuterClass.Line> {
        scope.launch { requests.map { Command(it) }.collect { mutableCommandFlow.emit(it) } }
        return lineFlow.map { it.build() }
    }

    override fun management(requests: Flow<WeaverOuterClass.Operation>): Flow<WeaverOuterClass.Notification> {
        scope.launch { requests.map { Operations(it) }.collect { mutableOperationFlow.emit(it) } }
        return notificationFlow.map { it.build() }
    }

    override fun onlinePlayers(request: Empty): Flow<Endervision.OnlinePlayersResponse> {
        return onlinePlayersFlow.map { it.build() }
    }
}