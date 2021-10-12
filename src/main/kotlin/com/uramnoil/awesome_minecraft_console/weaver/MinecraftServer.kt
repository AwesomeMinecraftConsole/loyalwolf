package com.uramnoil.awesome_minecraft_console.weaver

import com.uramnoil.awesome_minecraft_console.Command
import com.uramnoil.awesome_minecraft_console.Line
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.io.IOException
import kotlin.coroutines.CoroutineContext

class MinecraftServer(
    private val builder: ProcessBuilder,
    private val commandFlow: Flow<Command>,
    private val mutableLineSharedFlow: MutableSharedFlow<Line>,
    context: CoroutineContext
) : CoroutineScope by CoroutineScope(context + Job(context.job)) {
    private lateinit var process: Process
    private val input by lazy {
        process.inputReader()
    }
    private val output by lazy {
        process.outputWriter()
    }
    private val error by lazy {
        process.errorReader()
    }

    fun start() {
        process = builder.start()
        launch {
            commandFlow.collect {
                writeLine(it.value)
            }
        }
        launch(coroutineContext + Dispatchers.IO) {
            receiveEachLine().collect {
                mutableLineSharedFlow.emit(Line(it))
            }
        }
    }

    private suspend fun receiveEachLine(): Flow<String> {
        return flow {
            try {
                while (true) {
                    input.readLine()?.let { emit(it) } ?: break
                }
                shutdown()
            } catch (error: IOException) {
                shutdown()
            }
        }
    }

    private fun writeLine(line: String) {
        output.run {
            kotlin.runCatching {
                write(line)
                appendLine()
                flush()
            }.onFailure {
                shutdown()
            }
        }
    }

    fun shutdown() {
        process.apply {
            input.close()
            output.close()
            error.close()
        }
        process.destroy()
        coroutineContext.cancelChildren()
    }

    suspend fun joinUntilShutdown() {
        process.joinUntilShutdown()
    }
}

suspend fun Process.joinUntilShutdown() = withContext(Dispatchers.Default) { waitFor() }