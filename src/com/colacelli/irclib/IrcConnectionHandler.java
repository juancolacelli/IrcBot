package com.colacelli.irclib;

import java.io.IOException;

public abstract class IrcConnectionHandler {
    protected IrcConnection ircConnection;
    
    public void setIrcConnection(IrcConnection ircConnection) {
        this.ircConnection = ircConnection;
    }
    
    protected abstract void onChannelMessage(IrcChannelMessage message) throws IOException;
    protected abstract void onConnect(IrcServer server, IrcUser user) throws IOException;
    protected abstract void onDisconnect(IrcServer server) throws IOException;
    protected abstract void onJoin(IrcUser user, IrcChannel channel) throws IOException;
    protected abstract void onKick(IrcUser user, IrcChannel channel) throws IOException;
    protected abstract void onMode(IrcChannel channel, String mode) throws IOException;
    protected abstract void onNickChange(IrcUser user) throws IOException;
    protected abstract void onPart(IrcUser user, IrcChannel channel) throws IOException;
    protected abstract void onPing() throws IOException;
    protected abstract void onPrivateMessage(IrcPrivateMessage message) throws IOException;
}
