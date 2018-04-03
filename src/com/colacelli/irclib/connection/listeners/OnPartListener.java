package com.colacelli.irclib.connection.listeners;

import com.colacelli.irclib.actors.IrcChannel;
import com.colacelli.irclib.actors.IrcUser;
import com.colacelli.irclib.connection.IrcConnection;

public abstract class OnPartListener {
    public abstract void onPart(IrcConnection ircConnection, IrcUser user, IrcChannel channel);
}
