package com.uramnoil.awesome_minecraft_console

import awesome_minecraft_console.acrobat.AcrobatOuterClass
import awesome_minecraft_console.endervision.Endervision
import awesome_minecraft_console.weaver.WeaverOuterClass

data class Line(val value: String) {
    fun build() = WeaverOuterClass.Line.newBuilder().setLine(value).build()
}

fun Line(line: WeaverOuterClass.Line) = Line(line.line)

data class Command(val value: String) {
    fun build() = WeaverOuterClass.Command.newBuilder().setCommand(value).build()
}

fun Command(command: WeaverOuterClass.Command): Command {
    return Command(command.command)
}

enum class Operations(val value: Int) {
    Start(0),
    Unrecognized(-1),
}

fun Operations(operation: WeaverOuterClass.Operation): Operations {
    return when (operation.operationValue) {
        0 -> Operations.Start
        else -> Operations.Unrecognized
    }
}

data class Notification(val value: String) {
    fun build() = WeaverOuterClass.Notification.newBuilder().setNotification(value).build()
}

fun Notification(notification: WeaverOuterClass.Notification): Notification {
    return Notification(notification.notification)
}

data class OnlinePlayer(val id: String, val name: String, val ping: Int) {
    fun build(): AcrobatOuterClass.OnlinePlayer {
        return AcrobatOuterClass
            .OnlinePlayer
            .newBuilder()
            .setName(name)
            .setId(id)
            .setPing(this@OnlinePlayer.ping)
            .build()
    }
}

fun Collection<OnlinePlayer>.build(): Endervision.OnlinePlayersResponse {
    return Endervision.OnlinePlayersResponse.newBuilder().apply { addAllOnlinePlayers(map(OnlinePlayer::build)) }
        .build()
}