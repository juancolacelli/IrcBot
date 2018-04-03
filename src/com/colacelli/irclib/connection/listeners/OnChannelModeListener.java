package com.colacelli.irclib.connection.listeners;

import com.colacelli.irclib.actors.Channel;
import com.colacelli.irclib.connection.Connection;

public interface OnChannelModeListener {
    void onChannelMode(Connection connection, Channel channel, String mode);
}
