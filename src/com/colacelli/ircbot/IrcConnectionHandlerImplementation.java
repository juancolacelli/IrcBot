package com.colacelli.ircbot;

import java.io.IOException;
import java.util.Arrays;

import com.colacelli.irclib.actor.IrcUser;
import com.colacelli.irclib.actor.IrcChannel;
import com.colacelli.irclib.message.IrcChannelMessage;
import com.colacelli.irclib.connection.IrcConnection;
import com.colacelli.irclib.connection.IrcConnectionHandler;
import com.colacelli.irclib.connection.IrcServer;
import com.colacelli.irclib.message.IrcPrivateMessage;

public class IrcConnectionHandlerImplementation extends IrcConnectionHandler {
    @Override
    public void onConnect(IrcConnection ircConnection, IrcServer server, IrcUser user) throws IOException {
        System.out.println("Connected to " + server.getHostname() + ":" + server.getPort() + " as: " + user.getNick() + ":" + user.getLogin());
        
        ircConnection.joinChannel(new IrcChannel(Configurable.CHANNEL));
    }

    @Override
    public void onDisconnect(IrcConnection ircConnection, IrcServer server) throws IOException {
        System.out.println("Disconnecting from " + server.getHostname() + ":" + server.getPort());
    }

    @Override
    public void onJoin(IrcConnection ircConnection, IrcUser user, IrcChannel channel) throws IOException {
        System.out.println(user.getNick() + " joined " + channel.getName());
        
        if(!user.getNick().equals(ircConnection.getCurrentUser().getNick()))
            ircConnection.sendChannelMessage(new IrcChannelMessage(
                    ircConnection.getCurrentUser(),
                    channel,
                    "Hello " + user.getNick() + " welcome to " + channel.getName()
            ));
    }

    @Override
    public void onKick(IrcConnection ircConnection, IrcUser user, IrcChannel channel) throws IOException {
        System.out.println(user.getNick() + " has been kicked from " + channel.getName());
        
        ircConnection.joinChannel(new IrcChannel(Configurable.CHANNEL));
    }

    @Override
    public void onChannelMessage(IrcConnection ircConnection, IrcChannelMessage message) throws IOException {
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
                    ircConnection.sendPrivateMessage(new IrcPrivateMessage(
                            ircConnection.getCurrentUser(),
                            new IrcUser(sender),
                            "Joining " + parameters[0]
                    ));
                    ircConnection.joinChannel(new IrcChannel(parameters[0]));
                }
                
                break;
            case "!part":
                String partChannel = channel.getName();
                
                if(parameters != null)
                    partChannel = parameters[0];
                
                if(!partChannel.equals("")) {
                    ircConnection.sendPrivateMessage(new IrcPrivateMessage(
                        ircConnection.getCurrentUser(),
                        new IrcUser(sender),
                        "Parting from " + partChannel)
                    );
                    ircConnection.partFromChannel(new IrcChannel(partChannel));
                }
                
                break;
            case "!op":
                String opNick = sender;
                if(parameters != null)
                    opNick    = parameters[0];
                
                ircConnection.sendPrivateMessage(new IrcPrivateMessage(
                        ircConnection.getCurrentUser(),
                        new IrcUser(sender),
                        "Giving OP to " + opNick + " in " + channel.getName()
                ));
                ircConnection.changeMode(channel, "+o " + opNick);
                
                break;
            case "!deop":
                String deopNick = sender;
                if(parameters != null)
                    deopNick    = parameters[0];
                
                ircConnection.sendPrivateMessage(new IrcPrivateMessage(
                        ircConnection.getCurrentUser(),
                        new IrcUser(sender),
                        "Removing OP to " + deopNick + " in " + channel.getName()
                ));
                ircConnection.changeMode(channel, "-o " + deopNick);
                
                break;
            case "!voice":
                String voiceNick = sender;
                if(parameters != null)
                    voiceNick    = parameters[0];
                
                ircConnection.sendPrivateMessage(new IrcPrivateMessage(
                        ircConnection.getCurrentUser(),
                        new IrcUser(sender),
                        "Giving VOICE to " + voiceNick + " in " + channel.getName()
                ));
                ircConnection.changeMode(channel, "+v " + voiceNick);
                
                break;
            case "!devoice":
                String devoiceNick = sender;
                if(parameters != null)
                    devoiceNick    = parameters[0];
                
                ircConnection.sendPrivateMessage(new IrcPrivateMessage(
                        ircConnection.getCurrentUser(),
                        new IrcUser(sender),
                        "Removing VOICE to " + devoiceNick + " in " + channel.getName()
                ));
                ircConnection.changeMode(channel, "-v " + devoiceNick);
                
                break;
        }
    }

    @Override
    public void onMode(IrcConnection ircConnection, IrcChannel channel, String mode) throws IOException {
        System.out.println("Mode changed to " + mode + " in " + channel.getName());
    }

    @Override
    public void onNickChange(IrcConnection ircConnection, IrcUser user) throws IOException {
        System.out.println(user.getOldNick() + " changed nickname to " + user.getNick());
    }

    @Override
    public void onPart(IrcConnection ircConnection, IrcUser user, IrcChannel channel) throws IOException {
        System.out.println(user.getNick() + " parted from " + channel.getName());
    }

    @Override
    public void onPing(IrcConnection ircConnection) {
        System.out.println("PING!");
    }

    @Override
    public void onPrivateMessage(IrcConnection ircConnection, IrcPrivateMessage message) throws IOException {
        String sender = message.getSender().getNick();
        String text   = message.getText();

        System.out.println("Private message received from " + sender + ": " + text);

        ircConnection.sendPrivateMessage(new IrcPrivateMessage(
                ircConnection.getCurrentUser(),
                new IrcUser(sender),
                text
        ));
    }
}
