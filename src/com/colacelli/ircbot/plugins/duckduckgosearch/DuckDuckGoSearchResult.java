package com.colacelli.ircbot.plugins.duckduckgosearch;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class DuckDuckGoSearchResult {
    private static final String DUCK_DUCK_GO_URL = "https://api.duckduckgo.com/?q=QUERY&format=json";
    private static final String DUCK_DUCK_GO_JSON_TITLE = "Heading";
    private static final String DUCK_DUCK_GO_JSON_TEXT = "AbstractText";
    private static final String DUCK_DUCK_GO_JSON_SOURCE = "AbstractSource";
    private static final String DUCK_DUCK_GO_JSON_URL = "AbstractURL";

    private String title;
    private String text;
    private String source;
    private String url;

    private DuckDuckGoSearchResult(String title, String text, String source, String url) {
        this.title = title;
        this.text = text;
        this.source = source;
        this.url = url;
    }

    public static DuckDuckGoSearchResult get(String query) {
        String url = DUCK_DUCK_GO_URL.replace("QUERY", query);

        DuckDuckGoSearchResult result = null;

        try {
            StringBuilder jsonText = new StringBuilder();

            InputStream response = new URL(url).openStream();
            Scanner scanner = new Scanner(response).useDelimiter("\\A");
            while (scanner.hasNext()) {
                jsonText.append(scanner.next());
            }

            JSONObject json = (JSONObject) new JSONParser().parse(jsonText.toString());
            String title = (String) json.get(DUCK_DUCK_GO_JSON_TITLE);
            String text = (String) json.get(DUCK_DUCK_GO_JSON_TEXT);
            String source = (String) json.get(DUCK_DUCK_GO_JSON_SOURCE);
            String link = (String) json.get(DUCK_DUCK_GO_JSON_URL);

            result = new DuckDuckGoSearchResult(title, text, source, link);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    public String toString() {
        StringBuilder resultText = new StringBuilder();
        if (source != null && !source.isEmpty()) {
            resultText.append("[");
            resultText.append(source);
            resultText.append("] ");
        }

        resultText.append(title);

        if (text != null && !text.isEmpty()) {
            resultText.append(": ");
            resultText.append(text.split("\\.")[0]);
        }

        resultText.append(" - ");
        resultText.append(url);

        return resultText.toString();
    }

    public boolean isEmpty() {
        return url == null || url.isEmpty();
    }
}
