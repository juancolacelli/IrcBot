package com.colacelli.irclib.connection.listeners;

import com.colacelli.irclib.connection.IrcConnection;

public abstract class OnPingListener {
    public abstract void onPing(IrcConnection ircConnection);
}
