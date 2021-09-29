import com.uramnoil.awesome_minecraft_console.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow

suspend fun main(args: Array<String>) {
    val mutableLineSharedFlow = MutableSharedFlow<Line>()
    val mutableCommandSharedFlow = MutableSharedFlow<Command>()
    val mutableOperationSharedFlow = MutableSharedFlow<Operations>()
    val mutableNotificationSharedFlow = MutableSharedFlow<Notification>()
    val mutableOnlinePlayersSharedFlow = MutableSharedFlow<OnlinePlayers>()

    val context = Job() + CoroutineName("LoyalWolfMain")
    val scope = CoroutineScope(context)

    val enderVisionServerPort = System.getenv().getOrDefault("LOYALWOLF_ENDERVISION_PORT", "50051").toUShort()
    val weaverAndAcrobatServerPort = System.getenv().getOrDefault("LOYALWOLF_WEAVER_AND_ACROBAT_PORT", "50052").toUShort()

    val enderVisionServer = scope.async {
        val server = EnderVisionServer(
            enderVisionServerPort,
            mutableLineSharedFlow,
            mutableCommandSharedFlow,
            mutableNotificationSharedFlow,
            mutableOperationSharedFlow,
            mutableOnlinePlayersSharedFlow,
            context
        )
        server.start()
        server.joinUntilShutdown()
    }
    val weaverAndAcrobatServer = scope.async {
        val server = WeaverAndAcrobatServer(
            weaverAndAcrobatServerPort,
            mutableLineSharedFlow,
            mutableCommandSharedFlow,
            mutableNotificationSharedFlow,
            mutableOperationSharedFlow,
            mutableOnlinePlayersSharedFlow,
            context
        )
        server.start()
        server.joinUntilShutdown()
    }

    val servers = listOf(enderVisionServer, weaverAndAcrobatServer)
    servers.awaitAll()
}