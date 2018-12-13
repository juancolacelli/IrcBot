package com.colacelli.ircbot.plugins.autoresponse

import com.colacelli.ircbot.plugins.access.IRCBotAccess
import com.colacelli.ircbot.plugins.help.PluginHelp

class AutoResponsePluginHelp(command : String, access : IRCBotAccess.Level, help : String, vararg args : String) : PluginHelp(command, access, help, *args) {
    companion object {
        const val SEPARATOR = "|"
    }

    override fun toString() : String {
        var text = "$command "

        for (i in 0 until args.size) {
            val it = args[i]

            if (i > 0) text += SEPARATOR

            text += "<$it>"
        }

        text += ": $help"

        return text
    }
}