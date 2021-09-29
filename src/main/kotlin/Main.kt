import com.uramnoil.awesome_minecraft_console.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow

suspend fun main(args: Array<String>) {
    val mutableLineSharedFlow = MutableSharedFlow<Line>()
    val mutableCommandSharedFlow = MutableSharedFlow<Command>()
    val mutableOperationSharedFlow = MutableSharedFlow<Operations>()
    val mutableNotificationSharedFlow = MutableSharedFlow<Notification>()
    val mutableOnlinePlayersSharedFlow = MutableSharedFlow<OnlinePlayers>()

    val scope = CoroutineScope(GlobalScope.coroutineContext + CoroutineName("LoyalWolfMain"))

    val enderVisionServer = scope.async {
        val server = EnderVisionServer(
            50052.toShort(),
            mutableLineSharedFlow,
            mutableCommandSharedFlow,
            mutableNotificationSharedFlow,
            mutableOperationSharedFlow,
            mutableOnlinePlayersSharedFlow,
            scope.coroutineContext
        )
        server.start()
    }
    val weaverAndAcrobatServer = scope.async {
        val server = WeaverAndAcrobatServer(
            50052.toShort(),
            mutableLineSharedFlow,
            mutableCommandSharedFlow,
            mutableNotificationSharedFlow,
            mutableOperationSharedFlow,
            mutableOnlinePlayersSharedFlow,
            scope.coroutineContext
        )
        server.start()
    }

    val servers = listOf(enderVisionServer, weaverAndAcrobatServer)
    servers.awaitAll()
}