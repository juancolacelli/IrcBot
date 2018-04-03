package com.colacelli.irclib.connection.listeners;

import com.colacelli.irclib.actors.IrcUser;
import com.colacelli.irclib.connection.IrcConnection;

public interface OnNickChangeListener {
    void onNickChange(IrcConnection ircConnection, IrcUser user);
}
