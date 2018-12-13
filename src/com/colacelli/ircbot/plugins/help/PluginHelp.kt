package com.colacelli.ircbot.plugins.help

import com.colacelli.ircbot.plugins.access.IRCBotAccess

open class PluginHelp(val command : String, val access : IRCBotAccess.Level, val help : String, vararg val args : String) {
    override fun toString() : String {
        var text = command

        args.forEach {
            text += " <$it>"
        }

        text += ": $help"

        return text
    }
}