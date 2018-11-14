package com.colacelli.ircbot.plugins.websitetitle;

public interface OnWebsiteTitleGetListener {
    void onSuccess(String url, String title);

    void onError();
}
