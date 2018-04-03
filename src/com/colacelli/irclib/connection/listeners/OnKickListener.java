package com.colacelli.irclib.connection.listeners;

import com.colacelli.irclib.actors.Channel;
import com.colacelli.irclib.actors.User;
import com.colacelli.irclib.connection.Connection;

public interface OnKickListener {
    void onKick(Connection connection, User user, Channel channel);
}
