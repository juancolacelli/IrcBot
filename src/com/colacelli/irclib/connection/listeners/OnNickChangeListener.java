package com.colacelli.irclib.connection.listeners;

import com.colacelli.irclib.actors.User;
import com.colacelli.irclib.connection.Connection;

public interface OnNickChangeListener {
    void onNickChange(Connection connection, User user);
}
