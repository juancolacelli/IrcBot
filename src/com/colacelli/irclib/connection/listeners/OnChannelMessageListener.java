package com.colacelli.irclib.connection.listeners;

import com.colacelli.irclib.connection.Connection;
import com.colacelli.irclib.messages.ChannelMessage;

public interface OnChannelMessageListener {
    void onChannelMessage(Connection connection, ChannelMessage message);
}
