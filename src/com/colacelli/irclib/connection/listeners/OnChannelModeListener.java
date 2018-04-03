package com.colacelli.irclib.connection.listeners;

import com.colacelli.irclib.actors.IrcChannel;
import com.colacelli.irclib.connection.IrcConnection;

public abstract class OnChannelModeListener {
    public abstract void onChannelMode(IrcConnection ircConnection, IrcChannel channel, String mode);
}
