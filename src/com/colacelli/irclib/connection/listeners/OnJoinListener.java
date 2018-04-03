package com.colacelli.irclib.connection.listeners;

import com.colacelli.irclib.actors.IrcChannel;
import com.colacelli.irclib.actors.IrcUser;
import com.colacelli.irclib.connection.IrcConnection;

public interface OnJoinListener {
    void onJoin(IrcConnection ircConnection, IrcUser user, IrcChannel channel);
}
