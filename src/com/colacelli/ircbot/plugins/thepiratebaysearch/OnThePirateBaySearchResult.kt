package com.colacelli.ircbot.plugins.thepiratebaysearch

interface OnThePirateBaySearchResult {
    fun onSuccess(result: ThePirateBaySearchPlugin.ThePirateBaySearchResult)
    fun onError(result: ThePirateBaySearchPlugin.ThePirateBaySearchResult)
}
