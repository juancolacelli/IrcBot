package com.colacelli.irclib.connection.listeners;

import com.colacelli.irclib.connection.IrcConnection;
import com.colacelli.irclib.message.IrcChannelMessage;

import java.io.IOException;

public abstract class OnChannelMessageListener {
   public abstract void onChannelMessage(IrcConnection ircConnection, IrcChannelMessage message);
}
