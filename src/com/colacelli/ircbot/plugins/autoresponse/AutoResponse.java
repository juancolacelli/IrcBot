package com.colacelli.ircbot.plugins.autoresponse;

import com.colacelli.irclib.messages.ChannelMessage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Properties;

public class AutoResponse {
    private static final String PROPERTIES_FILE = "autoresponse.properties";

    private static AutoResponse instance;
    private Properties properties;

    private AutoResponse() {
        properties = new Properties();
    }

    public static AutoResponse getInstance() {
        if (instance == null) {
            instance = new AutoResponse();
        }

        return instance;
    }

    public void setAutoResponse(String trigger, String response) {
        loadProperties();
        properties.setProperty(trigger.toUpperCase(), response);
        saveProperties();
    }

    public String getAutoResponse(ChannelMessage message) {
        loadProperties();

        String text = message.getText();
        String response = properties.getProperty(text.toUpperCase());

        if (response != null && !response.isEmpty()) {
            response = response.replace("$nick", message.getSender().getNick());
            response = response.replace("$channel", message.getChannel().getName());
        }

        return response;
    }

    public HashMap<String, String> getAutoResponses() {

        HashMap<String, String> autoResponses = new HashMap<>();
        properties.forEach((key, value) -> {
            String response = String.valueOf(value);
            if (!response.isEmpty()) autoResponses.put(String.valueOf(key), response);
        });

        return autoResponses;
    }

    private void loadProperties() {
        try {
            FileInputStream fileInputStream = new FileInputStream(PROPERTIES_FILE);
            properties.load(fileInputStream);
        } catch (IOException e) {
            // Properties file not found
            properties = new Properties();
            saveProperties();
        }
    }

    private void saveProperties() {
        OutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(PROPERTIES_FILE);
            properties.store(outputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
