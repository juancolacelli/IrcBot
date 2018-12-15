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
        var helps = ArrayList<Help>()

        bot.listeners.forEach {
            if (it.command.startsWith(command) && it.level.value <= level.value) helps.add(it.help)
        }

        // Get max command length + 5
        val whitespaces = helps.sortedBy { it.commandWithArgs!!.length }.reversed()[0].commandWithArgs!!.length

        helps.forEach {
            texts.add(it.toText(whitespaces))
        }

        texts.sort()
        return texts
    }
}