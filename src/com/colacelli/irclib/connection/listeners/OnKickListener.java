package com.colacelli.irclib.connection.listeners;

import com.colacelli.irclib.actor.IrcChannel;
import com.colacelli.irclib.actor.IrcUser;
import com.colacelli.irclib.connection.IrcConnection;

public abstract class OnKickListener {
    public abstract void onKick(IrcConnection ircConnection, IrcUser user, IrcChannel channel);
}
