package com.colacelli.irclib.connection.listeners;

import com.colacelli.irclib.actor.IrcChannel;
import com.colacelli.irclib.actor.IrcUser;
import com.colacelli.irclib.connection.IrcConnection;

public abstract class OnJoinListener {
    public abstract void onJoin(IrcConnection ircConnection, IrcUser user, IrcChannel channel);
}
