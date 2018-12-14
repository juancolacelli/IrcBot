package com.colacelli.ircbot.listeners

import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.messages.ChannelMessage

interface OnChannelCommandListener {
    val commands: Array<String>
    fun onChannelCommand(connection : Connection, message : ChannelMessage, command : String, args : Array<String>)
}