package com.colacelli.ircbot.plugins.duckduckgosearch

interface OnDuckDuckGoSearchResultListener {
    fun onSuccess(searchResult: DuckDuckGoSearchPlugin.DuckDuckGoSearchResult)
    fun onError()
}
