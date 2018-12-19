package com.colacelli.ircbot.base.listeners

import com.colacelli.ircbot.base.Access
import com.colacelli.ircbot.base.Help
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.messages.ChannelMessage

interface OnChannelCommandListener {
    val command: String
    val aliases: Array<String>?
    val level: Access.Level
    val help: Help

    fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>)
}