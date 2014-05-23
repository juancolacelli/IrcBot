package com.colacelli.ircbot;

import java.io.IOException;
import java.util.Arrays;

import com.colacelli.irclib.IrcChannel;
import com.colacelli.irclib.IrcChannelMessage;
import com.colacelli.irclib.IrcConnectionHandler;
import com.colacelli.irclib.IrcPrivateMessage;
import com.colacelli.irclib.IrcServer;
import com.colacelli.irclib.IrcUser;

public class IrcConnectionHandlerImplementation extends IrcConnectionHandler {
    @Override
    public void onConnect(IrcServer server, IrcUser user) throws IOException {
        System.out.println("Connected to " + server.getHostname() + ":" + server.getPort() + " as: " + user.getNick() + ":" + user.getLogin());
        
        transport.joinChannel(Configurable.CHANNEL);
    }

    @Override
    public void onDisconnect(IrcServer server) throws IOException {
        System.out.println("Disconnecting from " + server.getHostname() + ":" + server.getPort());
    }

    @Override
    public void onJoin(IrcUser user, IrcChannel channel) throws IOException {
        System.out.println(user.getNick() + " joined " + channel.getName());
        
        if(!user.getNick().equals(transport.getCurrentUser().getNick()))
            transport.sendChannelMessage(channel.getName(), "Hello " + user.getNick() + " welcome to " + channel.getName());
    }

    @Override
    public void onKick(IrcUser user, IrcChannel channel) throws IOException {
        System.out.println(user.getNick() + " has been kicked from " + channel.getName());
        
        transport.joinChannel(Configurable.CHANNEL);
    }

    @Override
    public void onChannelMessage(IrcChannelMessage message) throws IOException {
        String sender      = message.getSender().getNick();
        String text        = message.getText();
        IrcChannel channel = message.getChannel();
        
        System.out.println("Message received from " + sender + ": " + text + " in " + channel.getName());
        
        String[] splittedMessage = text.split(" ");
        String command           = splittedMessage[0];
        String[] parameters      = null;
        
        if(splittedMessage.length > 1)
            parameters           = Arrays.copyOfRange(splittedMessage, 1, splittedMessage.length);
        
        switch(command) {
            case "!join":
                if(parameters != null) {
                    transport.sendPrivateMessage(sender, "Joining " + parameters[0]);
                    transport.joinChannel(parameters[0]);
                }
                
                break;
            case "!part":
                String partChannel = channel.getName();
                
                if(parameters != null)
                    partChannel = parameters[0];
                
                if(partChannel != "") {
                    transport.sendPrivateMessage(sender, "Parting from " + partChannel);
                    transport.partFromChannel(partChannel);    
                }
                
                break;
            case "!op":
                String opNick = sender;
                if(parameters != null)
                    opNick    = parameters[0];
                
                transport.sendPrivateMessage(sender, "Giving OP to " + opNick + " in " + channel.getName());
                transport.changeMode(channel.getName(), "+o " + opNick);
                
                break;
            case "!deop":
                String deopNick = sender;
                if(parameters != null)
                    deopNick    = parameters[0];
                
                transport.sendPrivateMessage(sender, "Removing OP to " + deopNick + " in " + channel.getName());
                transport.changeMode(channel.getName(), "-o " + deopNick);
                
                break;
            case "!voice":
                String voiceNick = sender;
                if(parameters != null)
                    voiceNick    = parameters[0];
                
                transport.sendPrivateMessage(sender, "Giving VOICE to " + voiceNick + " in " + channel.getName());
                transport.changeMode(channel.getName(), "+v " + voiceNick);
                
                break;
            case "!devoice":
                String devoiceNick = sender;
                if(parameters != null)
                    devoiceNick    = parameters[0];
                
                transport.sendPrivateMessage(sender, "Removing VOICE to " + devoiceNick + " in " + channel.getName());
                transport.changeMode(channel.getName(), "-v " + devoiceNick);
                
                break;
        }
    }

    @Override
    public void onMode(IrcChannel channel, String mode) throws IOException {
        System.out.println("Mode changed to " + mode + " in " + channel.getName());
    }

    @Override
    public void onNickChange(IrcUser user) throws IOException {
        System.out.println(user.getOldNick() + " changed nickname to " + user.getNick());
    }

    @Override
    public void onPart(IrcUser user, IrcChannel channel) throws IOException {
        System.out.println(user.getNick() + " parted from " + channel.getName());
    }

    @Override
    public void onPing() {
        System.out.println("PING!");
    }

    @Override
    public void onPrivateMessage(IrcPrivateMessage message) throws IOException {
        String sender      = message.getSender().getNick();
        String text        = message.getText();
        
        System.out.println("Private message received from " + sender + ": " + text);
    }
}
