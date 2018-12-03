package com.colacelli.ircbot.plugins.thepiratebaysearch;

public interface OnThePirateBaySearchResult {
    void onSuccess(ThePirateBaySearchResult result);
    void onError();
}
