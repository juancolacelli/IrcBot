package com.colacelli.ircbot;

public abstract class IrcConnectionHandler {
    @SuppressWarnings("unused")
    private IrcConnection transport;
    
    public void setTransport(IrcConnection transport) {
        this.transport = transport;
    }
    
    public abstract void onConnect(String server, int port);
    public abstract void onDisconnect(String server, int port);
    public abstract void onJoin(String channel);
    public abstract void onKick(String nick, String channel);
    public abstract void onLogin(String nick, String login);
    public abstract void onMessage(String sender, String message);
    public abstract void onNick(String nick);
    public abstract void onPart(String channel);
    public abstract void onPing();
}
