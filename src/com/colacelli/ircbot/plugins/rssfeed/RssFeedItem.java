package com.colacelli.ircbot.plugins.rssfeed;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RssFeedItem {
    private String guid;
    private String title;
    private String url;
    private String description;
    private long createdAt;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMM yyyy hh:mm:ss +S");
        try {
            Date date = simpleDateFormat.parse(createdAt);
            this.createdAt = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            this.createdAt = 0;
        }
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String toString() {
        return title + " - " + url;
    }
}
