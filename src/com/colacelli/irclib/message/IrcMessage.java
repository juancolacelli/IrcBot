package com.colacelli.irclib.message;

import com.colacelli.irclib.actor.IrcUser;

public abstract class IrcMessage {
    protected IrcUser sender;
    protected String text;

    public IrcUser getSender() {
        return sender;
    }

    public void setSender(IrcUser newSender) {
        sender = newSender;
    }

    public String getText() {
        return text;
    }
}
