package com.uramnoil.awesome_minecraft_console

import awesome_minecraft_console.weaver.WeaverGrpcKt
import awesome_minecraft_console.weaver.WeaverOuterClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class WeaverService(
    private val mutableLineFlow: MutableSharedFlow<Line>,
    private val commandFlow: Flow<Command>,
    private val mutableNotificationFlow: MutableSharedFlow<Notification>,
    private val operationFlow: Flow<Operations>,
    coroutineContext: CoroutineContext
) : WeaverGrpcKt.WeaverCoroutineImplBase(), CoroutineScope by CoroutineScope(coroutineContext) {
    override fun console(requests: Flow<WeaverOuterClass.Line>): Flow<WeaverOuterClass.Command> {
        launch {
            requests.map { Line(it) }.collect { mutableLineFlow.emit(it) }
        }
        return commandFlow.map { it.build() }
    }

    override fun management(requests: Flow<WeaverOuterClass.Notification>): Flow<WeaverOuterClass.Operation> {
        launch {
            requests.map { Notification(it) }.collect { mutableNotificationFlow.emit(it) }
        }
        return operationFlow.map { it.build() }
    }
}