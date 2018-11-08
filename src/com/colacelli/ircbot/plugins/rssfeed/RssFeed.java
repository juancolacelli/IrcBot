package com.colacelli.ircbot.plugins.rssfeed;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class RssFeed {
    private static final String ITEM_TAG = "item";
    private static final String TITLE_TAG = "title";
    private static final String LINK_TAG = "link";
    private static final String DESCRIPTION_TAG = "description";

    private ArrayList<RssFeedItem> rssFeedItems;

    private String url;
    private String lastUrl;

    public RssFeed(String url) {
        this.url = url;
    }

    public ArrayList<RssFeedItem> check() {
        rssFeedItems = new ArrayList<>();

        try {
            InputStream response = new URL(url).openStream();

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(response);

            NodeList items = document.getElementsByTagName(ITEM_TAG);

            for (int i = 0; i < items.getLength(); i++) {
                Node item = items.item(i);
                NodeList nodes = item.getChildNodes();

                RssFeedItem rssFeedItem = new RssFeedItem();

                for (int j = 0; j < nodes.getLength(); j++) {
                    Node node = nodes.item(j);

                    switch (node.getNodeName()) {
                        case TITLE_TAG:
                            rssFeedItem.setTitle(node.getTextContent());
                            break;
                        case LINK_TAG:
                            rssFeedItem.setUrl(node.getTextContent());
                            break;
                        case DESCRIPTION_TAG:
                            rssFeedItem.setDescription(node.getTextContent());
                            break;
                    }
                }

                if (!rssFeedItem.getUrl().equals(lastUrl)) {
                    rssFeedItems.add(rssFeedItem);
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        if (!rssFeedItems.isEmpty()) {
            lastUrl = rssFeedItems.get(0).getUrl();
        }

        return rssFeedItems;
    }

    public ArrayList<RssFeedItem> getItems() {
        return rssFeedItems;
    }
}
