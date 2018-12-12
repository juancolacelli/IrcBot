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

        // @JvmStatic
        // fun getInstance() : PluginHelper {
        //     return Singleton.instance
        // }
    }

    fun addHelp(help : PluginHelp) {
        helps.add(help)
    }

    fun removeHelp(help : PluginHelp) {
        helps.remove(help)
    }

    fun removeHelp(command : String) {
        helps.forEach {
            if (it.command.toUpperCase() == command.toUpperCase()) {
                helps.remove(it)
            }
        }
    }

    fun getCommands(access : IRCBotAccess.Level = IRCBotAccess.Level.USER) : ArrayList<String> {
        helps.sortBy { it.command }

        var commands = ArrayList<String>()
        helps.forEach {
            if (it.access.level < access.level) commands.add(it.command)
        }

        return commands
    }

    fun getHelp(access : IRCBotAccess.Level = IRCBotAccess.Level.USER, command : String) : ArrayList<String> {
        var texts = ArrayList<String>()
        helps.forEach {
            if (it.help.startsWith(command) && it.access.level < access.level) texts.add(it.toString())
        }

        return texts
    }
}