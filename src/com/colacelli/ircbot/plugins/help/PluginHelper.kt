package com.colacelli.ircbot.plugins.help

import com.colacelli.ircbot.plugins.access.IRCBotAccess

class PluginHelper {
    private val helps = ArrayList<PluginHelp>()

    private object Singleton {
        val instance = PluginHelper()
    }

    companion object {
        val instance by lazy {
            Singleton.instance
        }
    }

    fun addHelp(help : PluginHelp) {
        helps.add(help)
        helps.sortBy { it.command }
    }

    fun removeHelp(help : PluginHelp) {
        helps.remove(help)
    }

    fun removeHelp(command : String) {
        helps.removeAll(helps.filter {
            it.command.toLowerCase() == command.toLowerCase()
        })
    }

    fun getCommands(access : IRCBotAccess.Level = IRCBotAccess.Level.USER) : ArrayList<String> {
        val commands = ArrayList<String>()
        helps.forEach {
            // Prevent dupes
            if (commands.indexOf(it.baseCommand) == -1) {
                if (it.access.level < access.level) commands.add(it.baseCommand)
            }
        }

        return commands
    }

    fun getHelp(access : IRCBotAccess.Level = IRCBotAccess.Level.USER, command : String) : ArrayList<String> {
        val texts = ArrayList<String>()
        helps.forEach {
            if (it.command.startsWith(command) && it.access.level <= access.level) texts.add(it.toString())
        }

        return texts
    }
}