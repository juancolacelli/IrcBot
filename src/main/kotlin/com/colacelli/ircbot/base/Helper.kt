package com.colacelli.ircbot.base

class Helper {
    private val helps = ArrayList<Help>()

    fun list(level: Access.Level, query: String): ArrayList<Array<String>> {
        val texts = ArrayList<Array<String>>()
        helps.filter {
            // FIXME: Allow get help from aliases
            (query.isBlank() || it.command.startsWith(query)) && it.level.value <= level.value
        }.forEach {
            // FIXME: Dirty...
            var aliases = ""
            if (it.aliases != null) {
                aliases = it.aliases.joinToString(" ")
            }
            texts.add(arrayOf(it.command, it.args.joinToString(" "), aliases, it.help, it.level.toString()))
        }

        return texts
    }

    fun addHelp(help: Help) {
        helps.add(help)
    }

    fun removeHelp(help: Help) {
        helps.remove(help)
    }
}