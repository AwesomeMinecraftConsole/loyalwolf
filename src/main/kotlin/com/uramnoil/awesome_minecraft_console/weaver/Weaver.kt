package com.uramnoil.awesome_minecraft_console.weaver

import com.uramnoil.awesome_minecraft_console.Command
import com.uramnoil.awesome_minecraft_console.Line
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.io.File
import kotlin.coroutines.CoroutineContext

class Weaver(
    startSh: File,
    private val mutableSharedCommandFlow: MutableSharedFlow<Command>,
    private val mutableSharedLineFlow: MutableSharedFlow<Line>,
    context: CoroutineContext
) : CoroutineScope by CoroutineScope(context + Job(context.job)) {
    private val launcher: MinecraftServerLauncher =
        MinecraftServerLauncher(
            startSh,
            mutableSharedCommandFlow,
            mutableSharedLineFlow,
            shouldLoop = true,
            coroutineContext
        )

    fun start() {
        launcher.start()
        launch {
            mutableSharedLineFlow.collect {
                println(it.value)
            }
        }
        launch(Dispatchers.IO) {
            redirectSystemInput().collect {
                if (it.isAwesomeCommand()) {
                    executeAwesomeCommand(it.split(' ').drop(1))
                } else {
                    mutableSharedCommandFlow.emit(Command(it))
                }
            }
        }
    }

    fun shutdown() {
        launcher.start()
    }

    suspend fun joinUntilShutdown() {
        coroutineContext.job.join()
    }

    private suspend fun redirectSystemInput(): Flow<String> {
        val inputBufferedReader = System.`in`.bufferedReader()
        return flow {
            while (true) {
                emit(inputBufferedReader.readLine())
            }
        }
    }


    private fun executeAwesomeCommand(args: List<String>) {
        when (args.getOrNull(0)) {
            "loopoff" -> {
                launcher.shouldLoop = false
                println("Server Loop: off")
            }
            "loopon" -> {
                launcher.shouldLoop = true
                println("Server Loop: on")
            }
            "stop" -> shutdown()
            "start" -> launcher.start()
            else -> {}
        }
    }
}


fun String.isAwesomeCommand() = startsWith("awesome")