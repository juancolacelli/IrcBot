package com.colacelli.irclib;

public class IrcMessage {
    private IrcUser sender;
    private IrcUser receiver;
    private String text;
    private IrcChannel channel;
    
    IrcMessage(IrcUser user, String text, IrcChannel channel) {
        this.sender    = user;
        this.text    = text;
        this.channel = channel;
    }
    
    IrcMessage(IrcChannel channel, String text) {
        this.text    = text;
        this.channel = channel;
    }
    
    public IrcMessage(IrcUser receiver, String text) {
        this.text     = text;
        this.receiver = receiver;
    }
    
    public IrcUser getSender() {
        return this.sender;
    }
    
    public String getText() {
        return this.text;
    }
        
    public IrcChannel getChannel() {
        return this.channel;
    }
    
    
    public IrcUser getReceiver() {
        return this.receiver;
    }
    
    public Boolean isPrivate() {
    	return this.channel != null;
    }
}
