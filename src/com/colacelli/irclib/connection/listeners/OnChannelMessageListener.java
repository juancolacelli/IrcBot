package com.colacelli.irclib.connection.listeners;

import com.colacelli.irclib.connection.IrcConnection;
import com.colacelli.irclib.messages.IrcChannelMessage;

public interface OnChannelMessageListener {
    void onChannelMessage(IrcConnection ircConnection, IrcChannelMessage message);
}
