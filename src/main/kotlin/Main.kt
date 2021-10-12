import com.uramnoil.awesome_minecraft_console.*
import com.uramnoil.awesome_minecraft_console.weaver.Weaver
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import java.io.File

suspend fun main(args: Array<String>) {
    val mutableLineSharedFlow = MutableSharedFlow<Line>()
    val mutableCommandSharedFlow = MutableSharedFlow<Command>()
    val mutableOperationSharedFlow = MutableSharedFlow<Operations>()
    val mutableNotificationSharedFlow = MutableSharedFlow<Notification>()
    val mutableOnlinePlayersSharedFlow = MutableSharedFlow<OnlinePlayers>()

    val context = Dispatchers.Default + Job() + CoroutineName("LoyalWolfMain")
    val scope = CoroutineScope(context)

    val weaver = Weaver(
        File(
            System.getenv().get("LOYALWOLF_STARTSH_PATH")
                ?: error("The environment variable \"LOYALWOLF_STARTSH_PATH\" has not been set. Please set the absolute path of start.sh to \"LOYALWOLF_STARTSH_PATH\".")
        ),
        mutableCommandSharedFlow,
        mutableLineSharedFlow, context
    )
    weaver.start()

    val enderVisionServerPort = System.getenv().getOrDefault("LOYALWOLF_ENDERVISION_PORT", "50051").toUShort()
    val acrobatServerPort = System.getenv().getOrDefault("LOYALWOLF_WEAVER_AND_ACROBAT_PORT", "50052").toUShort()

    val endervisionServer = EnderVisionServer(
        enderVisionServerPort,
        mutableLineSharedFlow,
        mutableCommandSharedFlow,
        mutableNotificationSharedFlow,
        mutableOperationSharedFlow,
        mutableOnlinePlayersSharedFlow,
        context
    )
    endervisionServer.start()

    val acrobatServer = AcrobatServer(
        acrobatServerPort,
        mutableOnlinePlayersSharedFlow,
        context
    )
    acrobatServer.start()

    val weaverDeferred = scope.async {
        weaver.joinUntilShutdown()
    }

    val enderVisionDeferred = scope.async {
        endervisionServer.joinUntilShutdown()
    }

    val acrobatDeferred = scope.async {
        acrobatServer.joinUntilShutdown()
    }

    val deferreds = listOf(weaverDeferred, enderVisionDeferred, acrobatDeferred)
    deferreds.awaitAll()
}