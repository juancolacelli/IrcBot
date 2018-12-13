package com.colacelli.ircbot.plugins.rssfeed

internal interface OnRSSFeedCheckListener {
    fun onSuccess(rssFeedItem: RSSFeedPlugin.RSSFeedItem)
    fun onError(url: String)
}
