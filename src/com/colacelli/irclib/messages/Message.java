package com.colacelli.irclib.messages;

import com.colacelli.irclib.actors.User;

public abstract class Message {
    protected User sender;
    protected String text;

    public User getSender() {
        return sender;
    }

    public void setSender(User newSender) {
        sender = newSender;
    }

    public String getText() {
        return text;
    }
}
