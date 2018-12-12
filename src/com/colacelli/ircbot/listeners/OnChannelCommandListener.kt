package com.colacelli.ircbot.listeners

import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.messages.ChannelMessage

interface OnChannelCommandListener {
    fun channelCommand() : String
    fun onChannelCommand(connection : Connection, message : ChannelMessage, command : String, args : Array<String>)
}