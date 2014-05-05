package com.colacelli;

import com.colacelli.ircbot.IrcConnectionHandler;

public class IrcConnectionHandlerImplementation extends IrcConnectionHandler {
    @Override
    public void onConnect(String server, int port) {
        System.out.println("Connecting to " + server + ":" + port);
    }

    @Override
    public void onDisconnect(String server, int port) {
        System.out.println("Disconnecting from " + server + ":" + port);
    }

    @Override
    public void onJoin(String channel) {
        System.out.println("Joining " + channel);
    }

    @Override
    public void onKick(String nick, String channel) {
        System.out.println(nick + " has been kicked from " + channel);
        
    }

    @Override
    public void onLogin(String nick, String login) {
        System.out.println("Logging in as " + nick + ":" + login);
    }

    @Override
    public void onMessage(String sender, String message) {
        System.out.println("Message received from " + sender + ": " + message);
    }

    @Override
    public void onNick(String nick) {
        System.out.println("Changing nickname to " + nick);
    }

    @Override
    public void onPart(String channel) {
        System.out.println("Parting from " + channel);
    }

    @Override
    public void onPing() {
        System.out.println("PING!");
    }
}
