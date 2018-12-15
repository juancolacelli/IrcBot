package com.colacelli.ircbot.base

import com.colacelli.ircbot.IRCBot

class Helper(private val bot: IRCBot) {
    fun list(level: Access.Level): ArrayList<String> {
        var commands = ArrayList<String>()
        bot.listeners.forEach {
            // Prevent dupes
            if (commands.indexOf(it.command) == -1) {
                if (it.level.value < level.value) commands.add(it.command)
            }
        }
        commands.sort()
        return commands
    }

    fun get(level: Access.Level, command: String): ArrayList<String> {
        val texts = ArrayList<String>()
        bot.listeners.forEach {
            if (it.command.startsWith(command) && it.level.value <= level.value) texts.add(it.help.toString())
        }
        texts.sort()
        return texts
    }
}