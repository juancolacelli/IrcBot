package com.colacelli.irclib.connection.listeners;

import com.colacelli.irclib.actors.IrcChannel;
import com.colacelli.irclib.connection.IrcConnection;

public interface OnChannelModeListener {
    void onChannelMode(IrcConnection ircConnection, IrcChannel channel, String mode);
}
