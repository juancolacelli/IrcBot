package com.colacelli.irclib;

public class IrcMessage {
    protected IrcUser sender;
    protected IrcUser receiver;
    protected String text;
    
    public IrcUser getSender() {
        return sender;
    }
    
    public String getText() {
        return text;
    }    
    
    public IrcUser getReceiver() {
        return receiver;
    }
    
    public void setSender(IrcUser newSender) {
        sender = newSender;
    }
}
