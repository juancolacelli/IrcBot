package com.colacelli.irclib;

import java.io.IOException;

public abstract class IrcConnectionHandler {
    @SuppressWarnings("unused")
    protected IrcConnection transport;
    
    public void setTransport(IrcConnection transport) {
        this.transport = transport;
    }
    
    public abstract void onConnect(String server, int port, String nick, String login) throws IOException;
    public abstract void onDisconnect(String server, int port) throws IOException;
    public abstract void onJoin(String channel) throws IOException;
    public abstract void onKick(String nick, String channel) throws IOException;
    public abstract void onMessage(String sender, String message, String channel) throws IOException;
    public abstract void onMode(String channel, String mode) throws IOException;
    public abstract void onNick(String nick) throws IOException;
    public abstract void onPart(String channel) throws IOException;
    public abstract void onPing() throws IOException;
}
