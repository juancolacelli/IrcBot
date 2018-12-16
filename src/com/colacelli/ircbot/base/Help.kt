package com.colacelli.ircbot.base

open class Help(val help: String, vararg val args: String) {
    var commandWithArgs : String? = null
    var command: String? = null
        set(value) {
            commandWithArgs = value + " " + args.joinToString(" ")
            field = value
        }
}