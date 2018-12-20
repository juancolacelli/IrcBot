package com.colacelli.ircbot.base

import com.colacelli.ircbot.base.listeners.OnChannelCommandListener

open class Help(listener: OnChannelCommandListener, val help: String, vararg val args: String) {
    val command = listener.command
    val aliases = listener.aliases
    val level = listener.level

    init {
        aliases?.sort()
    }
}