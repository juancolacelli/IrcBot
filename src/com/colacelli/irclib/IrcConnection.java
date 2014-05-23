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
    private IrcServer server;
    
    private IrcUser user;
    private HashMap<String, IrcChannel> channels = new HashMap<String, IrcChannel>();
    
    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;
    private IrcConnectionHandler handler;
    
    public IrcConnection(IrcConnectionHandler newHandler) {
        handler = newHandler;
        handler.setTransport(this);
    }
    
    public void connectToServer(String hostname, int port, String password, String nick, String login) throws IOException {
        connectToServer(new IrcServer(hostname, port, password), new IrcUser(nick, login));
    }
    
    private void connectToServer(IrcServer newServer, IrcUser newUser) throws IOException {
//        try {
            user   = newUser;
            server = newServer;
            
            socket = new Socket(server.getHostname(), server.getPort());
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            if(!server.getPassword().equals("")) {
                writer.write("PASS " + server.getPassword() + ENTER);
                writer.flush();    
            }
            
            loginToServer(user);
//        } catch(Exception e) {
//            reconnectToServer();
//        }
    }
    
    public void disconnectFromServer() throws IOException {
        socket.close();
        
        handler.onDisconnect(server);
    }
    
    public void joinChannel(String channelName) throws IOException {
        joinChannel(new IrcChannel(channelName));
    }
    
    private void joinChannel(IrcChannel channel) throws IOException {
        writer.write("JOIN " + channel.getName() + ENTER);
        writer.flush();
        
        if(channels.get(channel.getName()) == null)
            channels.put(channel.getName(), channel);    
    }
    
    private void listenServer() throws IOException {
        // Keep reading lines from the server.
        String line = null;
        
        while((line = reader.readLine()) != null) {
            System.out.println(line);

            String[] splittedLine = line.split(" ");
            try {
                int rawCode = Integer.parseInt(splittedLine[1]);
                if(rawCode == RawCode.LOGGED_IN.getCode()) {
                    handler.onConnect(server, user);
                } else if(rawCode == RawCode.NICKNAME_IN_USE.getCode()) {
                    // Re-login with a random ending
                    changeNick(user.getNick() + (new Random()).nextInt(9));
                }  
            } catch(NumberFormatException e) {}    
            
            if(line.toLowerCase().startsWith("ping ")) {               
                writer.write("PONG " + line.substring(5) + ENTER);
                writer.flush();
                
                handler.onPing();
            } else {
                IrcChannel channel = null;
                
                if(splittedLine[2].indexOf("#") != -1)
                    channel = channels.get(splittedLine[2]);
                
                switch(splittedLine[1]) {
                    case "PRIVMSG":
                        int senderIndex  = line.indexOf("!");
                        int messageIndex = line.indexOf(":", 1);
                        
                        if(senderIndex != -1 && messageIndex != -1) {
                            String sender  = line.substring(1, senderIndex);
                            String text    = line.substring(messageIndex + 1);
                            IrcMessage ircMessage = new IrcMessage(new IrcUser(sender), text, channel);
                            
                            if(channel != null)
                                handler.onMessage(ircMessage);
                            else
                                handler.onPrivateMessage(ircMessage);
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
    
    
    public void sendMessage(String channelName, String messageText) throws IOException {
        IrcChannel channel = channels.get(channelName);
        
        if(channel != null) {
            sendMessage(new IrcMessage(channel, messageText));
        }
    }
    
    public void sendPrivateMessage(String receiverNick, String messageText) throws IOException {
        sendMessage(new IrcMessage(new IrcUser(receiverNick), messageText));
    }

    private void sendMessage(IrcMessage message) throws IOException {
        writer.write("PRIVMSG " + (message.isPrivate() ? message.getChannel().getName() : message.getReceiver().getNick()) + " :" + message.getText() + ENTER);
        writer.flush();
        
        if(message.getSender() == null)
            message.setSender(user);

        handler.onMessage(message);
    }
    
    public void changeMode(String channelName, String mode) throws IOException {
        IrcChannel channel = channels.get(channelName);
        
        if(channel != null) {
            changeMode(channel, mode);
        }
    }
    
    private void changeMode(IrcChannel channel, String mode) throws IOException {
        writer.write("MODE " + channel.getName() + " " + mode + ENTER);
        writer.flush();
        
        handler.onMode(channel, mode);
    }
    
    public void changeNick(String nick) throws IOException {
        user.setNick(nick);
        
        writer.write("NICK " + nick + ENTER);
        writer.flush();
        
        handler.onNickChange(user);
    }

    public void partFromChannel(String channelName) throws IOException {
        IrcChannel channel = channels.get(channelName);
        
        if(channel != null) {
            partFromChannel(channel);
        }
    }
    
    private void partFromChannel(IrcChannel channel) throws IOException {
        writer.write("PART " + channel.getName() + ENTER);
        writer.flush();
        
        if(channels.get(channel.getName()) != null)
            channels.remove(channel.getName());
        
        handler.onPart(user, channel);    
    }
    
    public void reconnectToServer() throws IOException {
        disconnectFromServer();
        connectToServer(server, user);
    }
    
    public IrcUser getUser() {
        return user;
    }
}
