package com.colacelli.irclib.connection.listeners;

import com.colacelli.irclib.connection.IrcConnection;
import com.colacelli.irclib.messages.IrcPrivateMessage;

public interface OnPrivateMessageListener {
    void onPrivateMessage(IrcConnection ircConnection, IrcPrivateMessage message);
}
