package com.colacelli.irclib;

import java.io.IOException;

public abstract class IrcConnectionHandler {
    protected IrcConnection transport;
    
    public void setTransport(IrcConnection transport) {
        this.transport = transport;
    }
    
    public abstract void onConnect(IrcServer server, String nick, String login) throws IOException;
    public abstract void onDisconnect(IrcServer server) throws IOException;
    public abstract void onJoin(IrcChannel channel) throws IOException;
    public abstract void onKick(IrcUser user, IrcChannel channel) throws IOException;
    public abstract void onMessage(IrcMessage message) throws IOException;
    public abstract void onMode(IrcChannel channel, String mode) throws IOException;
    public abstract void onNick(String nick) throws IOException;
    public abstract void onPart(IrcChannel channel) throws IOException;
    public abstract void onPing() throws IOException;
}
