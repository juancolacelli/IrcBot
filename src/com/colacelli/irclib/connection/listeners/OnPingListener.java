package com.colacelli.irclib.connection.listeners;

import com.colacelli.irclib.connection.Connection;

public interface OnPingListener {
    void onPing(Connection connection);
}
