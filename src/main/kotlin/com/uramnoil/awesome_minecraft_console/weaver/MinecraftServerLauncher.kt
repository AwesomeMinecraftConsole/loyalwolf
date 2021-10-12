package com.uramnoil.awesome_minecraft_console.weaver

import com.uramnoil.awesome_minecraft_console.Command
import com.uramnoil.awesome_minecraft_console.Line
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.io.File
import kotlin.coroutines.CoroutineContext

class MinecraftServerLauncher(
    startSh: File,
    private val commandFlow: Flow<Command>,
    private val mutableLineSharedFlow: MutableSharedFlow<Line>,
    var shouldLoop: Boolean,
    context: CoroutineContext,
) : CoroutineScope by CoroutineScope(context + Job(context.job)) {
    private val processBuilder = ProcessBuilder().apply {
        command("sh", startSh.absolutePath)
        directory(startSh.parentFile)
        redirectErrorStream(true)
    }

    private var server: MinecraftServer? = null

    private var _isRunning = false
    val isRunning: Boolean
        get() = _isRunning

    private suspend fun runServer() {
        val server = MinecraftServer(processBuilder, commandFlow, mutableLineSharedFlow, coroutineContext)
        this.server = server
        server.start()
        _isRunning = true
        server.joinUntilShutdown()
        _isRunning = false
        this.server = null
    }

    fun start() {
        if (isRunning) return
        _isRunning = true
        launch {
            do {
                runServer()
            } while (shouldLoop)
        }
        _isRunning = false
    }

    suspend fun joinUntilShutdown() {
        coroutineContext.job.join()
    }

    fun shutdown() {
        server?.shutdown()
        coroutineContext.cancelChildren()
    }
}
