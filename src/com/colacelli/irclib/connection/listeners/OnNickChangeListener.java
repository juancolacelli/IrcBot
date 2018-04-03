package com.colacelli.irclib.connection.listeners;

import com.colacelli.irclib.actor.IrcUser;
import com.colacelli.irclib.connection.IrcConnection;

public abstract class OnNickChangeListener {
    public abstract void onNickChange(IrcConnection ircConnection, IrcUser user);
}
