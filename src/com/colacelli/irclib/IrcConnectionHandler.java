package com.colacelli.irclib;

import java.io.IOException;

public abstract class IrcConnectionHandler {
    protected IrcConnection transport;
    
    public void setTransport(IrcConnection transport) {
        this.transport = transport;
    }
    
    public abstract void onConnect(IrcServer server, IrcUser user) throws IOException;
    public abstract void onDisconnect(IrcServer server) throws IOException;
    public abstract void onJoin(IrcUser user, IrcChannel channel) throws IOException;
    public abstract void onKick(IrcUser user, IrcChannel channel) throws IOException;
    public abstract void onMessage(IrcMessage message) throws IOException;
    public abstract void onMode(IrcChannel channel, String mode) throws IOException;
    public abstract void onNickChange(IrcUser user) throws IOException;
    public abstract void onPart(IrcUser user, IrcChannel channel) throws IOException;
    public abstract void onPing() throws IOException;
}
