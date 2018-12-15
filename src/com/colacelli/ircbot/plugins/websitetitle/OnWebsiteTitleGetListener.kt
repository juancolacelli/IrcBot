package com.colacelli.ircbot.plugins.websitetitle

interface OnWebsiteTitleGetListener {
    fun onSuccess(url: String, title: String)
    fun onError(url: String)
}