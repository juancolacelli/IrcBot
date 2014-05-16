package com.colacelli.irclib;

public class IrcMessage {
    private IrcUser user;
    private String text;
    private String channel;
    
    IrcMessage(IrcUser user, String text, String channel) {
        this.user    = user;
        this.text    = text;
        this.channel = channel;
    }
    
    public IrcUser getUser() {
        return this.user;
    }
    
    public String getText() {
        return this.text;
    }
        
    public String getChannel() {
        return this.channel;
    }
}
