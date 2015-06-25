package com.colacelli.irclib;

public class IrcChannelMessage extends IrcMessage {
    protected IrcChannel channel;

    public IrcChannelMessage(IrcUser newUser, IrcChannel newChannel, String newText) {
        sender  = newUser;
        text    = newText;
        channel = newChannel;
    }

    public IrcChannel getChannel() {
        return channel;
    }
}
