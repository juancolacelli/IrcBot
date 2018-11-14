package com.colacelli.ircbot.plugins.rssfeed;

import java.util.ArrayList;

interface OnRssFeedCheckListener {
    void onSuccess(RssFeed rssFeed, ArrayList<RssFeedItem> rssFeedItems);

    void onError(RssFeed rssFeed);
}
