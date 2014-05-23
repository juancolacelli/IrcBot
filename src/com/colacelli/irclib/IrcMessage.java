package com.colacelli.irclib;

public class IrcMessage {
    private IrcUser sender;
    private IrcUser receiver;
    private String text;
    private IrcChannel channel;
    
    public IrcMessage(IrcUser newUser, String newText, IrcChannel newChannel) {
        sender  = newUser;
        text    = newText;
        channel = newChannel;
    }
    
    public IrcMessage(IrcChannel newChannel, String newText) {
        channel = newChannel;
        text    = newText;
    }
    
    public IrcMessage(IrcUser newReceiver, String newText) {
        receiver = newReceiver;
        text     = newText;
    }
    
    public IrcUser getSender() {
        return sender;
    }
    
    public String getText() {
        return text;
    }
        
    public IrcChannel getChannel() {
        return channel;
    }
    
    
    public IrcUser getReceiver() {
        return receiver;
    }
    
    public Boolean isPrivate() {
        return channel != null;
    }
    
    public void setSender(IrcUser newSender) {
        sender = newSender;
    }
}
