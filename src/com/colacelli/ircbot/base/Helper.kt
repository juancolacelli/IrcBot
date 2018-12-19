package com.colacelli.ircbot.base

import com.colacelli.ircbot.IRCBot

class Helper(private val bot: IRCBot) {
    fun list(level: Access.Level, query: String): ArrayList<Array<String>> {
        var helps = ArrayList<Help>()

        // TODO: Don't use bot.listeners
        bot.listeners.forEach {
            if ((query.isBlank() || it.command.startsWith(query)) && it.level.value <= level.value) helps.add(it.help)
        }

        val texts = ArrayList<Array<String>>()
        helps.forEach {
            // FIXME: Dirty...
            var aliases = ""
            if (it.aliases != null) {
                it.aliases!!.sort()
                aliases = it.aliases!!.joinToString(" ")
            }
            texts.add(arrayOf(it.commandWithArgs!!, aliases, it.help))
        }

        return texts
    }
}