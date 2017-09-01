package com.colacelli.irclib.message;

import com.colacelli.irclib.actor.IrcChannel;
import com.colacelli.irclib.actor.IrcUser;

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
