package com.colacelli.ircbot.plugins.thepiratebaysearch;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class ThePirateBaySearchResult {
    private static final String THE_PIRATE_BAY_URL = "https://thepiratebay.online/s/?q=QUERY&page=0&orderby=99";

    private String title;
    private String url;
    private String magnet;
    private String uploadedAt;
    private String size;
    private int seeders;
    private int leechers;

    private ThePirateBaySearchResult() {
    }

    private ThePirateBaySearchResult(String title, String url, String magnet, String uploadedAt, String size, int seeders, int leechers) {
        this.title = title;
        this.url = url;
        this.magnet = magnet;
        this.uploadedAt = uploadedAt;
        this.size = size;
        this.seeders = seeders;
        this.leechers = leechers;
    }

    public static ThePirateBaySearchResult search(String query) {
        ThePirateBaySearchResult result = new ThePirateBaySearchResult();
        try {
            String url = THE_PIRATE_BAY_URL.replace("QUERY", query);

            Document document = Jsoup.connect(url).get();

            Element firstResult = document.select("table#searchResult tbody td:not(.vertTh)").first();
            Element link = firstResult.select("a").get(0);
            Element magnet = firstResult.select("a").get(1);
            Element description = firstResult.select("font.detDesc").first();
            Element parentTr = firstResult.parent();
            Element seeders = parentTr.select("td[align=right]").first();
            Element leechers = parentTr.select("td[align=right]").last();

            String[] descriptionText = description.text().split(", ");
            String size = descriptionText[1].replace("Size ", "");
            String uploadedAt = descriptionText[0].replace("Uploaded ", "");
            String magnetLink = magnet.attr("href").split("&")[0];

            result = new ThePirateBaySearchResult(
                    link.text(),
                    link.attr("href"),
                    magnetLink,
                    uploadedAt,
                    size,
                    Integer.parseInt(seeders.text()),
                    Integer.parseInt(leechers.text())
            );
        } catch (IOException e) {
            // Invalid URL
            return null;
        }
        return result;
    }

    public boolean isEmpty() {
        return magnet == null || magnet.isEmpty();
    }

    public String toString() {
        StringBuilder resultText = new StringBuilder();
        resultText.append(title);

        resultText.append(" [↑");
        resultText.append(uploadedAt);

        resultText.append("][↓");
        resultText.append(size);

        resultText.append("][⇅");
        resultText.append(seeders);
        resultText.append("S/");
        resultText.append(leechers);
        resultText.append("L] ");

        resultText.append(magnet);

        return resultText.toString();
    }
}
