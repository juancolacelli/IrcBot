package com.colacelli.irclib;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Random;

import com.colacelli.irclib.Rawable.RawCode;

public final class IrcConnection {
    private static final String ENTER =  "\r\n";
    private IrcServer currentServer;
    
    private IrcUser currentUser;
    private HashMap<String, IrcChannel> channelsJoined = new HashMap<>();
    
    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;
    private IrcConnectionHandler handler;
    
    public IrcConnection(IrcConnectionHandler newHandler) {
        handler = newHandler;
        handler.setTransport(this);
    }

    public void connectToServer(IrcServer newServer, IrcUser newUser) throws IOException {
        try {
            currentUser   = newUser;
            currentServer = newServer;
            
            socket = new Socket(currentServer.getHostname(), currentServer.getPort());
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            if(!currentServer.getPassword().equals("")) {
                writer.write("PASS " + currentServer.getPassword() + ENTER);
                writer.flush();    
            }
            
            loginToServer(currentUser);
        } catch(Exception e) {
            reconnectToServer();
        }
    }
    
    public void disconnectFromServer() throws IOException {
        socket.close();
        
        handler.onDisconnect(currentServer);
    }

    public void joinChannel(IrcChannel channel) throws IOException {
        writer.write("JOIN " + channel.getName() + ENTER);
        writer.flush();
        
        if(channelsJoined.get(channel.getName()) == null)
            channelsJoined.put(channel.getName(), channel);    
    }
    
    private void listenServer() throws IOException {
        // Keep reading lines from the server.
        String line;
        
        while((line = reader.readLine()) != null) {
            System.out.println(line);

            String[] splittedLine = line.split(" ");
            try {
                int rawCode = Integer.parseInt(splittedLine[1]);
                if(rawCode == RawCode.LOGGED_IN.getCode()) {
                    handler.onConnect(currentServer, currentUser);
                } else if(rawCode == RawCode.NICKNAME_IN_USE.getCode()) {
                    // Re-login with a random ending
                    changeNick(currentUser.getNick() + (new Random()).nextInt(9));
                }  
            } catch(NumberFormatException e) {
                // Not a RAW code
            }
            
            if(line.toLowerCase().startsWith("ping ")) {               
                writer.write("PONG " + line.substring(5) + ENTER);
                writer.flush();
                
                handler.onPing();
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
                                handler.onChannelMessage(new IrcChannelMessage(new IrcUser(nick, login), channel, text));
                            }
                            else {
                                handler.onPrivateMessage(new IrcPrivateMessage(new IrcUser(nick, login), currentUser, text));
                            }
                        }
                        
                        break;
                    case "JOIN":
                        if(channel != null)
                            handler.onJoin(new IrcUser(line.substring(1, line.indexOf("!"))), channel);

                        break;
                    case "KICK":
                        if(channel != null)
                            handler.onKick(new IrcUser(splittedLine[3]), channel);
                        
                        break;
                    case "MODE":
                        // FIXME: Mode is uncompleted, it just sends the first parameter.
                        if(channel != null)
                            handler.onMode(channel, splittedLine[3]);
                        
                        break;
                    case "NICK":
                        String oldNick   = line.substring(1, line.indexOf("!"));
                        IrcUser nickUser = new IrcUser(oldNick);
                        nickUser.setNick(splittedLine[2].substring(1));

                        handler.onNickChange(nickUser);

                        break;
                    case "PART":
                        if(channel != null)
                            handler.onPart(new IrcUser(line.substring(1, line.indexOf("!"))), channel);

                        break;
                }
            }
        }
    }
    
    private void loginToServer(IrcUser user) throws IOException {
        changeNick(user.getNick());
        
        writer.write("USER " + user.getLogin() + " 8 * : " + user.getLogin() + ENTER);
        writer.flush();
        
        listenServer();
    }

    public void sendChannelMessage(IrcChannelMessage ircChannelMessage) throws IOException {
        writer.write("PRIVMSG " + ircChannelMessage.getChannel().getName() + " :" + ircChannelMessage.getText() + ENTER);
        writer.flush();
        
        ircChannelMessage.setSender(currentUser);
    }

    public void sendPrivateMessage(IrcPrivateMessage ircPrivateMessage) throws IOException {
        writer.write("PRIVMSG " + ircPrivateMessage.getReceiver().getNick() + " :" + ircPrivateMessage.getText() + ENTER);
        writer.flush();
        
        ircPrivateMessage.setSender(currentUser);
    }

    public void changeMode(IrcChannel channel, String mode) throws IOException {
        writer.write("MODE " + channel.getName() + " " + mode + ENTER);
        writer.flush();
    }
    
    public void changeNick(String nick) throws IOException {
        currentUser.setNick(nick);
        
        writer.write("NICK " + nick + ENTER);
        writer.flush();
    }

    public void partFromChannel(IrcChannel channel) throws IOException {
        writer.write("PART " + channel.getName() + ENTER);
        writer.flush();
        
        if(channelsJoined.get(channel.getName()) != null)
            channelsJoined.remove(channel.getName());   
    }
    
    public void reconnectToServer() throws IOException {
        disconnectFromServer();
        connectToServer(currentServer, currentUser);
    }
    
    public IrcUser getCurrentUser() {
        return currentUser;
    }
}
