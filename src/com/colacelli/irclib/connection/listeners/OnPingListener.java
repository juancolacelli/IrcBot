package com.colacelli.irclib.connection.listeners;

import com.colacelli.irclib.connection.IrcConnection;

public interface OnPingListener {
    void onPing(IrcConnection ircConnection);
}
