package com.colacelli.irclib.connection.listeners;

import com.colacelli.irclib.connection.Connection;
import com.colacelli.irclib.messages.PrivateMessage;

public interface OnPrivateMessageListener {
    void onPrivateMessage(Connection connection, PrivateMessage message);
}
