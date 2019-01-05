package com.colacelli.ircbot.base

import com.colacelli.ircbot.IRCBot

class PluginLoader(val bot: IRCBot) {
    private val plugins = ArrayList<Plugin>()
    private val loadedPlugins = ArrayList<String>()

    fun add(plugin: Plugin, load: Boolean = true) {
        plugins.add(plugin)
        if (load) load(plugin.name)
    }

    fun load(name: String): Boolean {
        val plugin = find(name)
        return if (plugin != null && loadedPlugins.indexOf(name) == -1) {
            loadedPlugins.add(name)
            plugin.onLoad(bot)
            true
        } else {
            false
        }
    }

    fun unload(name: String): Boolean {
        val plugin = find(name)
        return if (plugin != null && loadedPlugins.indexOf(name) > -1) {
            loadedPlugins.remove(name)
            find(name)?.onUnload(bot)
            true
        } else {
            false
        }
    }

    fun list(loaded: Boolean = false): Array<String> {
        return if (loaded) {
            loadedPlugins.toTypedArray()
        } else {
            val names = ArrayList<String>()
            plugins.forEach {
                names.add(it.name)
            }
            names.toTypedArray()
        }
    }

    private fun find(name: String): Plugin? {
        return plugins.first {
            it.name == name
        }
    }
}