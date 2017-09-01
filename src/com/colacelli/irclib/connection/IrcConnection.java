package com.colacelli.irclib.connection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import com.colacelli.irclib.actor.IrcChannel;
import com.colacelli.irclib.message.IrcChannelMessage;
import com.colacelli.irclib.actor.IrcUser;
import com.colacelli.irclib.connection.Rawable.RawCode;
import com.colacelli.irclib.connection.connector.IrcConnector;
import com.colacelli.irclib.connection.connector.IrcSecureConnector;
import com.colacelli.irclib.connection.connector.IrcUnsecureConnector;
import com.colacelli.irclib.message.IrcPrivateMessage;

public final class IrcConnection {
    private IrcServer currentServer;
    
    private IrcUser currentUser;
    private HashMap<String, IrcChannel> channelsJoined = new HashMap<>();

    private IrcConnectionHandler handler;

    private IrcConnector ircConnector;
    
    public IrcConnection(IrcConnectionHandler newHandler) {
        handler = newHandler;
    }

    public void connectToServer(IrcServer newServer, IrcUser newUser) throws IOException {
        try {
            currentUser   = newUser;
            currentServer = newServer;

            if(currentServer.isSecure()) {
                ircConnector = new IrcSecureConnector();
            } else {
                ircConnector = new IrcUnsecureConnector();
            }

            ircConnector.connect(currentServer, currentUser);

            if(!currentServer.getPassword().equals("")) {
                ircConnector.send("PASS " + currentServer.getPassword());
            }
            
            loginToServer(currentUser);
        } catch(Exception e) {
            e.printStackTrace();
            reconnectToServer();
        }
    }
    
    public void disconnectFromServer() throws IOException {
        handler.onDisconnect(this, currentServer);
    }

    public void joinChannel(IrcChannel channel) throws IOException {
        ircConnector.send("JOIN " + channel.getName());

        if(channelsJoined.get(channel.getName()) == null)
            channelsJoined.put(channel.getName(), channel);    
    }
    
    private void listenServer() throws IOException {
        // Keep reading lines from the server.
        String line;
        
        while((line = ircConnector.listen()) != null) {
            System.out.println(line);

            String[] splittedLine = line.split(" ");
            try {
                int rawCode = Integer.parseInt(splittedLine[1]);
                if(rawCode == RawCode.LOGGED_IN.getCode()) {
                    handler.onConnect(this, currentServer, currentUser);
                } else if(rawCode == RawCode.NICKNAME_IN_USE.getCode()) {
                    // Re-login with a random ending
                    changeNick(currentUser.getNick() + (new Random()).nextInt(9));
                }  
            } catch(NumberFormatException e) {
                // Not a RAW code
            }
            
            if(line.toLowerCase().startsWith("ping ")) {               
                ircConnector.send("PONG " + line.substring(5));

                handler.onPing(this);
            } else {
                IrcChannel channel = null;
                
                if(splittedLine[2].contains("#"))
                    channel = channelsJoined.get(splittedLine[2]);
                
                switch(splittedLine[1]) {
                    case "PRIVMSG":
                        int nickIndex  = line.indexOf("!");
                        int loginIndex  = line.indexOf("@");
                        int messageIndex = line.indexOf(":", 1);
                        
                        if(nickIndex != -1 && messageIndex != -1) {
                            String nick  = line.substring(1, nickIndex);
                            String login = line.substring(1, loginIndex);
                            String text  = line.substring(messageIndex + 1);
                            
                            if(channel != null) {
                                handler.onChannelMessage(this, new IrcChannelMessage(new IrcUser(nick, login), channel, text));
                            }
                            else {
                                handler.onPrivateMessage(this, new IrcPrivateMessage(new IrcUser(nick, login), currentUser, text));
                            }
                        }
                        
                        break;
                    case "JOIN":
                        if(channel != null)
                            handler.onJoin(this, new IrcUser(line.substring(1, line.indexOf("!"))), channel);

                        break;
                    case "KICK":
                        if(channel != null)
                            handler.onKick(this, new IrcUser(splittedLine[3]), channel);
                        
                        break;
                    case "MODE":
                        // FIXME: Mode is uncompleted, it just sends the first parameter.
                        if(channel != null)
                            handler.onMode(this, channel, splittedLine[3]);
                        
                        break;
                    case "NICK":
                        String oldNick   = line.substring(1, line.indexOf("!"));
                        IrcUser nickUser = new IrcUser(oldNick);
                        nickUser.setNick(splittedLine[2].substring(1));

                        handler.onNickChange(this, nickUser);

                        break;
                    case "PART":
                        if(channel != null)
                            handler.onPart(this, new IrcUser(line.substring(1, line.indexOf("!"))), channel);

                        break;
                }
            }
        }
    }
    
    private void loginToServer(IrcUser user) throws IOException {
        changeNick(user.getNick());
        
        ircConnector.send("USER " + user.getLogin() + " 8 * : " + user.getLogin());

        listenServer();
    }

    public void sendChannelMessage(IrcChannelMessage ircChannelMessage) throws IOException {
        ircConnector.send("PRIVMSG " + ircChannelMessage.getChannel().getName() + " :" + ircChannelMessage.getText());

        ircChannelMessage.setSender(currentUser);
    }

    public void sendPrivateMessage(IrcPrivateMessage ircPrivateMessage) throws IOException {
        ircConnector.send("PRIVMSG " + ircPrivateMessage.getReceiver().getNick() + " :" + ircPrivateMessage.getText());

        ircPrivateMessage.setSender(currentUser);
    }

    public void changeMode(IrcChannel channel, String mode) throws IOException {
        ircConnector.send("MODE " + channel.getName() + " " + mode);
    }
    
    public void changeNick(String nick) throws IOException {
        currentUser.setNick(nick);
        
        ircConnector.send("NICK " + nick);
    }

    public void partFromChannel(IrcChannel channel) throws IOException {
        ircConnector.send("PART " + channel.getName());

        if(channelsJoined.get(channel.getName()) != null)
            channelsJoined.remove(channel.getName());   
    }
    
    public void reconnectToServer() throws IOException {
        ircConnector.disconnect();
        ircConnector.connect(currentServer, currentUser);
    }
    
    public IrcUser getCurrentUser() {
        return currentUser;
    }
}
