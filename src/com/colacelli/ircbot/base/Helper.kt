package com.colacelli.ircbot.base

import com.colacelli.ircbot.IRCBot

class Helper(private val bot: IRCBot) {
    fun list(level: Access.Level, query: String): HashMap<String, String> {
        var helps = ArrayList<Help>()

        bot.listeners.forEach {
            if ((query.isBlank() || it.command.startsWith(query)) && it.level.value <= level.value) helps.add(it.help)
        }

        val texts = HashMap<String, String>()
        helps.forEach {
            texts[it.commandWithArgs!!] = it.help
        }

        return texts
    }
}