package com.colacelli.irclib;

public class IrcChannelMessage extends IrcMessage {
    protected IrcChannel channel;
    
    public IrcChannelMessage(IrcUser newUser, IrcChannel newChannel, String newText) {
        sender  = newUser;
        text    = newText;
        channel = newChannel;
    }
    
    public IrcChannelMessage(IrcChannel newChannel, String newText) {
        channel = newChannel;
        text    = newText;
    }
    
    public IrcChannel getChannel() {
        return channel;
    }
}
