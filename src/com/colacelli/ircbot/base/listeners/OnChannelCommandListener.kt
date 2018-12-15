package com.colacelli.ircbot.base.listeners

import com.colacelli.ircbot.base.Access
import com.colacelli.ircbot.base.Help
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.messages.ChannelMessage

interface OnChannelCommandListener {
    var command: String
    var level: Access.Level
    var help: Help

    fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>)
}