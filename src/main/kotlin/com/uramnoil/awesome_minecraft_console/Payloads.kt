package com.uramnoil.awesome_minecraft_console

import awesome_minecraft_console.endervision.Loyalwolf

data class Line(val value: String) {
    fun build() = Loyalwolf.Line.newBuilder().setLine(value).build()
}

fun Line(line: Loyalwolf.Line) = Line(line.line)

data class Command(val value: String) {
    fun build() = Loyalwolf.Command.newBuilder().setCommand(value).build()
}

fun Command(command: Loyalwolf.Command): Command {
    return Command(command.command)
}

enum class Operations(private val value: Int) {
    Start(0),
    Unrecognized(-1), ;

    fun build() = Loyalwolf.Operation.newBuilder().setOperationValue(value).build()
}

fun Operations(operation: Loyalwolf.Operation): Operations {
    return when (operation.operationValue) {
        0 -> Operations.Start
        else -> Operations.Unrecognized
    }
}

data class Notification(val value: String) {
    fun build() = Loyalwolf.Notification.newBuilder().setNotification(value).build()
}

fun Notification(notification: Loyalwolf.Notification): Notification {
    return Notification(notification.notification)
}

data class OnlinePlayer(val id: String, val name: String, val ping: Int) {
    fun build(): Loyalwolf.OnlinePlayer {
        return Loyalwolf
            .OnlinePlayer
            .newBuilder()
            .setName(name)
            .setId(id)
            .setPing(this@OnlinePlayer.ping)
            .build()
    }
}

fun OnlinePlayer(onlinePlayer: Loyalwolf.OnlinePlayer): OnlinePlayer {
    return onlinePlayer.run { OnlinePlayer(id, name, ping) }
}

typealias OnlinePlayers = List<OnlinePlayer>

fun OnlinePlayers(onlinePlayers: Loyalwolf.OnlinePlayersRequest): OnlinePlayers {
    return onlinePlayers.onlinePlayersList.map { OnlinePlayer(it) }
}

fun OnlinePlayers.build(): Loyalwolf.OnlinePlayersResponse {
    return Loyalwolf.OnlinePlayersResponse.newBuilder().apply { addAllOnlinePlayers(map(OnlinePlayer::build)) }
        .build()
}