package com.uramnoil.awesome_minecraft_console.weaver

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter

class Console(private val bufferedReader: BufferedReader, private val bufferedWriter: BufferedWriter) {
    suspend fun readLine(): String? = withContext(Dispatchers.IO) {
        bufferedReader.readLine()
    }

    fun writeLine(line: String) {
        bufferedWriter.write(line)
        bufferedWriter.appendLine()
        bufferedWriter.flush()
    }

    fun shutdown() {
        bufferedReader.close()
        bufferedWriter.close()
    }
}