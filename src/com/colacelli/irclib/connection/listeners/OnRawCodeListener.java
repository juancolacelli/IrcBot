package com.colacelli.irclib.connection.listeners;

import com.colacelli.irclib.connection.Connection;

public interface OnRawCodeListener {
    void onRawCode(Connection connection, String message, int rawCode, String... args);
}
