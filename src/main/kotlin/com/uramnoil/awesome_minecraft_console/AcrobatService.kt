package com.uramnoil.awesome_minecraft_console

import awesome_minecraft_console.acrobat.AcrobatGrpcKt
import awesome_minecraft_console.acrobat.AcrobatOuterClass
import com.google.protobuf.Empty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlin.coroutines.CoroutineContext

private class AcrobatService(
    private val mutableOnlinePlayersFlow: MutableSharedFlow<OnlinePlayers>,
    context: CoroutineContext
) : AcrobatGrpcKt.AcrobatCoroutineImplBase(), CoroutineScope by CoroutineScope(context) {
    override suspend fun onlinePlayers(requests: Flow<AcrobatOuterClass.OnlinePlayersRequest>): Empty {
        requests.map { OnlinePlayers(it) }.collect {
            mutableOnlinePlayersFlow.emit(it)
        }
        return Empty.newBuilder().build()
    }
}