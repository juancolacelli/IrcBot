package com.colacelli.ircbot.plugins.duckduckgosearch;

public interface OnDuckDuckGoSearchResultListener {
    void onSuccess(DuckDuckGoSearchResult searchResult);

    void onError();
}
