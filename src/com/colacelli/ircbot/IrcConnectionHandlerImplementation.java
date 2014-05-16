package com.colacelli.ircbot;

import java.io.IOException;
import java.util.Arrays;

import com.colacelli.irclib.IrcChannel;
import com.colacelli.irclib.IrcConnectionHandler;
import com.colacelli.irclib.IrcMessage;
import com.colacelli.irclib.IrcServer;
import com.colacelli.irclib.IrcUser;

public class IrcConnectionHandlerImplementation extends IrcConnectionHandler {
    @Override
    public void onConnect(IrcServer server, String nick, String login) throws IOException {
        System.out.println("Connected to " + server.getHostname() + ":" + server.getPort() + " as: " + nick + ":" + login);
        
        transport.joinChannel(Configurable.CHANNEL);
    }

    @Override
    public void onDisconnect(IrcServer server) throws IOException {
        System.out.println("Disconnecting from " + server.getHostname() + ":" + server.getPort());
    }

    @Override
    public void onJoin(IrcChannel channel) throws IOException {
        System.out.println("Joining " + channel.getName());
    }

    @Override
    public void onKick(IrcUser user, IrcChannel channel) throws IOException {
        System.out.println(user.getNick() + " has been kicked from " + channel.getName());
        
        this.transport.joinChannel(Configurable.CHANNEL);
    }

    @Override
    public void onMessage(IrcMessage message) throws IOException {
        String sender      = message.getSender().getNick();
        String text        = message.getText();
        IrcChannel channel = message.getChannel();
        
        if(channel.getName() != "")
            System.out.println("Message received from " + sender + ": " + text + " in " + channel.getName());
        else
            System.out.println("Private message received from " + sender + ": " + text);
        
        String[] splittedMessage = text.split(" ");
        String command           = splittedMessage[0];
        String[] parameters      = null;
        
        if(splittedMessage.length > 1)
            parameters           = Arrays.copyOfRange(splittedMessage, 1, splittedMessage.length);
        
        switch(command) {
            case "!join":
                if(parameters != null) {
                    transport.sendMessage(new IrcMessage(new IrcUser(sender), "Joining " + parameters[0]));
                    transport.joinChannel(parameters[0]);
                }
                
                break;
            case "!part":
                String partChannel = channel.getName();
                
                if(parameters != null)
                    partChannel = parameters[0];
                
                if(partChannel != "") {
                    transport.sendMessage(new IrcMessage(new IrcUser(sender), "Parting from " + partChannel));
                    transport.partFromChannel(partChannel);    
                }
                
                break;
            case "!op":
                String opNick = sender;
                if(parameters != null)
                    opNick    = parameters[0];
                
                transport.sendMessage(new IrcMessage(new IrcUser(sender), "Giving OP to " + opNick + " in " + channel.getName()));
                transport.changeMode(channel.getName(), "+o " + opNick);
                
                break;
            case "!deop":
                String deopNick = sender;
                if(parameters != null)
                    deopNick    = parameters[0];
                
                transport.sendMessage(new IrcMessage(new IrcUser(sender), "Removing OP to " + deopNick + " in " + channel.getName()));
                transport.changeMode(channel.getName(), "-o " + deopNick);
                
                break;
            case "!voice":
                String voiceNick = sender;
                if(parameters != null)
                    voiceNick    = parameters[0];
                
                transport.sendMessage(new IrcMessage(new IrcUser(sender), "Giving VOICE to " + voiceNick + " in " + channel.getName()));
                transport.changeMode(channel.getName(), "+v " + voiceNick);
                
                break;
            case "!devoice":
                String devoiceNick = sender;
                if(parameters != null)
                    devoiceNick    = parameters[0];
                
                transport.sendMessage(new IrcMessage(new IrcUser(sender), "Removing VOICE to " + devoiceNick + " in " + channel.getName()));
                transport.changeMode(channel.getName(), "-v " + devoiceNick);
                
                break;
        }
    }

    @Override
    public void onMode(IrcChannel channel, String mode) throws IOException {
        System.out.println("Mode changed to " + mode + " in " + channel.getName());
    }

    @Override
    public void onNick(String nick) throws IOException {
        System.out.println("Changing nickname to " + nick);
    }

    @Override
    public void onPart(IrcChannel channel) throws IOException {
        System.out.println("Parting from " + channel.getName());
    }

    @Override
    public void onPing() {
        System.out.println("PING!");
    }
}
