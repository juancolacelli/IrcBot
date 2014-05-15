package com.colacelli.ircbot;

import java.io.IOException;
import java.util.Arrays;

import com.colacelli.irclib.IrcConnectionHandler;

public class IrcConnectionHandlerImplementation extends IrcConnectionHandler {
    @Override
    public void onConnect(String server, int port, String nick, String login) throws IOException {
        System.out.println("Connected to " + server + ":" + port + " as: " + nick + ":" + login);
        
        transport.join(Configurable.CHANNEL);
    }

    @Override
    public void onDisconnect(String server, int port) throws IOException {
        System.out.println("Disconnecting from " + server + ":" + port);
    }

    @Override
    public void onJoin(String channel) throws IOException {
        System.out.println("Joining " + channel);
    }

    @Override
    public void onKick(String nick, String channel) throws IOException {
        System.out.println(nick + " has been kicked from " + channel);
        
        this.transport.join(Configurable.CHANNEL);
    }

    @Override
    public void onMessage(String sender, String message, String channel) throws IOException {
        if(channel != "")
            System.out.println("Message received from " + sender + ": " + message + " in " + channel);
        else
            System.out.println("Private message received from " + sender + ": " + message);
        
        String[] splittedMessage = message.split(" ");
        String command              = splittedMessage[0];
        String[] parameters      = null;
        
        if(splittedMessage.length > 1)
            parameters              = Arrays.copyOfRange(splittedMessage, 1, splittedMessage.length);
        
        switch(command) {
            case "!join":
                if(parameters != null) {
                    transport.message(sender, "Joining " + parameters[0]);
                    transport.join(parameters[0]);
                }
                
                break;
            case "!part":
                String partChannel = channel;
                
                if(parameters != null)
                    partChannel = parameters[0];
                
                if(partChannel != "") {
                    transport.message(sender, "Parting from " + partChannel);
                    transport.part(partChannel);    
                }
                
                break;
            case "!op":
                String opNick = sender;
                if(parameters != null)
                    opNick      = parameters[0];
                
                transport.message(sender, "Giving OP to " + opNick + " in " + channel);
                transport.mode(channel, "+o " + opNick);
                
                break;
            case "!deop":
                String deopNick = sender;
                if(parameters != null)
                    deopNick    = parameters[0];
                
                transport.message(sender, "Removing OP to " + deopNick + " in " + channel);
                transport.mode(channel, "-o " + deopNick);
                
                break;
            case "!voice":
                String voiceNick = sender;
                if(parameters != null)
                    voiceNick     = parameters[0];
                
                transport.message(sender, "Giving VOICE to " + voiceNick + " in " + channel);
                transport.mode(channel, "+v " + voiceNick);
                
                break;
            case "!devoice":
                String devoiceNick = sender;
                if(parameters != null)
                    devoiceNick       = parameters[0];
                
                transport.message(sender, "Removing VOICE to " + devoiceNick + " in " + channel);
                transport.mode(channel, "-v " + devoiceNick);
                
                break;
        }
    }

    @Override
    public void onMode(String channel, String mode) throws IOException {
        System.out.println("Mode changed to " + mode + " in " + channel);
    }

    @Override
    public void onNick(String nick) throws IOException {
        System.out.println("Changing nickname to " + nick);
    }

    @Override
    public void onPart(String channel) throws IOException {
        System.out.println("Parting from " + channel);
    }

    @Override
    public void onPing() {
        System.out.println("PING!");
    }
}
