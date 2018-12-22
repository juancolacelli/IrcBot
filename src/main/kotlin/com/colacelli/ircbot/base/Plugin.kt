package com.colacelli.ircbot.base

import com.colacelli.ircbot.IRCBot

interface Plugin {
    var name: String
    fun onLoad(bot: IRCBot)
    fun onUnload(bot: IRCBot)
}