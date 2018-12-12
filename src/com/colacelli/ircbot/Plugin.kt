package com.colacelli.ircbot

interface Plugin {
    fun getName() : String
    fun onLoad(bot : IRCBot)
    fun onUnload(bot : IRCBot)
}