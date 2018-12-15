package com.colacelli.ircbot.base

open class Help(val help: String, vararg val args: String) {
    var command: String? = null

    override fun toString(): String {
        var text = command!!

        args.forEach {
            text += " $it"
        }

        text += ": $help"

        return text
    }
}