package com.colacelli.irclib.connection.listeners;

import com.colacelli.irclib.connection.IrcConnection;
import com.colacelli.irclib.message.IrcPrivateMessage;

public abstract class OnPrivateMessageListener {
    public abstract void onPrivateMessage(IrcConnection ircConnection, IrcPrivateMessage message);
}
