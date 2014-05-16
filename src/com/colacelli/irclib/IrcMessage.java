package com.colacelli.irclib;

public class IrcMessage {
    private IrcUser sender;
    private IrcUser receiver;
    private String text;
    private IrcChannel channel;
    
    public IrcMessage(IrcUser user, String text, IrcChannel channel) {
        this.sender  = user;
        this.text    = text;
        this.channel = channel;
    }
    
    public IrcMessage(IrcChannel channel, String text) {
        this.channel = channel;
    	this.text    = text;
    }
    
    public IrcMessage(IrcUser receiver, String text) {
        this.receiver = receiver;
        this.text     = text;
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
    
    public void setSender(IrcUser sender) {
    	this.sender = sender;
    }
}
