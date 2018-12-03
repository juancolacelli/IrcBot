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
    private String description;
    private int seeders;
    private int leechers;

    private ThePirateBaySearchResult() {}

    private ThePirateBaySearchResult(String title, String url, String magnet, String description, int seeders, int leechers) {
        this.title = title;
        this.url = url;
        this.magnet = magnet;
        this.description = description;
        this.seeders = seeders;
        this.leechers = leechers;
    }

    public static ThePirateBaySearchResult search(String query) {
        ThePirateBaySearchResult result = new ThePirateBaySearchResult();
        try {
            String url = THE_PIRATE_BAY_URL.replace("QUERY", query);

            Document doc = Jsoup.connect(url).get();

            Element firstResult = doc.select("table#searchResult tbody td:not(.vertTh)").first();
            Element link = firstResult.select("a").get(0);
            Element magnet = firstResult.select("a").get(1);
            Element description = firstResult.select("font.detDesc").first();
            Element parentTr = firstResult.parent();
            Element seeders = parentTr.select("td[align=right]").first();
            Element leechers = parentTr.select("td[align=right]").last();

            result = new ThePirateBaySearchResult(
                    link.text(),
                    link.attr("href"),
                    magnet.attr("href"),
                    description.text(),
                    Integer.parseInt(seeders.text()),
                    Integer.parseInt(leechers.text())
            );
        } catch (IOException e) {
            // Invalid URL
        }
        return result;
    }

    public boolean isEmpty() {
        return magnet == null || magnet.isEmpty();
    }

    public String toString() {
        StringBuilder resultText = new StringBuilder();
        resultText.append(title);
        resultText.append(": ");
        resultText.append(description);

        resultText.append(" (");
        resultText.append(seeders);
        resultText.append("s/");
        resultText.append(leechers);
        resultText.append("l) - ");

        resultText.append(url);

        return resultText.toString();
    }
}
