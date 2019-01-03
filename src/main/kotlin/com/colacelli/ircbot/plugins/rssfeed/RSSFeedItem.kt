package com.colacelli.ircbot.plugins.rssfeed

class RSSFeedItem(val rssFeedUrl: String, val url: String, val title: String, var hasNewContent: Boolean = false)
