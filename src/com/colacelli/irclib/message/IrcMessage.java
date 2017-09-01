package com.colacelli.irclib.message;

import com.colacelli.irclib.actor.IrcUser;

public class IrcMessage {
    protected IrcUser sender;
    protected String text;
    
    public IrcUser getSender() {
        return sender;
    }
    
    public String getText() {
        return text;
    }   
    
    public void setSender(IrcUser newSender) {
        sender = newSender;
    }
}
