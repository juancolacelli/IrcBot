package com.colacelli.ircbot.plugins.search.duckduckgo

import com.google.gson.annotations.SerializedName

class DuckDuckGoSearchResult(@SerializedName("Heading") val title: String, @SerializedName("AbstractText") val text: String, @SerializedName("AbstractSource") val source: String, @SerializedName("AbstractURL") val url: String)