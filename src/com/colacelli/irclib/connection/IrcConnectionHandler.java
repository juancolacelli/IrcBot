package com.colacelli.irclib.connection;

import com.colacelli.irclib.actor.IrcChannel;
import com.colacelli.irclib.message.IrcChannelMessage;
import com.colacelli.irclib.actor.IrcUser;
import com.colacelli.irclib.message.IrcPrivateMessage;

import java.io.IOException;

public abstract class IrcConnectionHandler {
    protected abstract void onChannelMessage(IrcConnection ircConnection, IrcChannelMessage message) throws IOException;
    protected abstract void onConnect(IrcConnection ircConnection, IrcServer server, IrcUser user) throws IOException;
    protected abstract void onDisconnect(IrcConnection ircConnection, IrcServer server) throws IOException;
    protected abstract void onJoin(IrcConnection ircConnection, IrcUser user, IrcChannel channel) throws IOException;
    protected abstract void onKick(IrcConnection ircConnection, IrcUser user, IrcChannel channel) throws IOException;
    protected abstract void onMode(IrcConnection ircConnection, IrcChannel channel, String mode) throws IOException;
    protected abstract void onNickChange(IrcConnection ircConnection, IrcUser user) throws IOException;
    protected abstract void onPart(IrcConnection ircConnection, IrcUser user, IrcChannel channel) throws IOException;
    protected abstract void onPing(IrcConnection ircConnection) throws IOException;
    protected abstract void onPrivateMessage(IrcConnection ircConnection, IrcPrivateMessage message) throws IOException;
}
