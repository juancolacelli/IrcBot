package com.colacelli.irclib;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Random;

import com.colacelli.irclib.Rawable.RawCode;

public final class IrcConnection {
    private static final String ENTER =  "\r\n";
    private IrcServer server;
    
    private IrcUser user;
    
    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;
    private IrcConnectionHandler handler;
    
    public IrcConnection(IrcConnectionHandler handler) {
        this.handler = handler;
        this.handler.setTransport(this);
    }
    
    public void connectToServer(IrcServer server, IrcUser user) throws IOException {
        try {
            this.user   = user;
            this.server = server;
            
            socket = new Socket(server.getHostname(), server.getPort());
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            if(!server.getPassword().equals("")) {
                writer.write("PASS " + server.getPassword() + ENTER);
                writer.flush();    
            }
            
            loginToServer(this.user);
        } catch(Exception e) {
            reconnectToServer();
        }
    }
    
    public void disconnectFromServer() throws IOException {
        socket.close();
        
        handler.onDisconnect(this.server);
    }
    
    public void joinChannel(IrcChannel channel) throws IOException {
        writer.write("JOIN " + channel.getName() + ENTER);
        writer.flush();
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
                switch(splittedLine[1]) {
                    case "PRIVMSG":
                        int senderIndex  = line.indexOf("!");
                        int messageIndex = line.indexOf(":", 1);
                        
                        if(senderIndex != -1 && messageIndex != -1) {
                            String privmsgSender  = line.substring(1, senderIndex);
                            String privmsgText    = line.substring(messageIndex + 1);
                            String privmsgChannel = "";
                            
                            if(splittedLine[2].indexOf("#") != -1)
                                privmsgChannel    = splittedLine[2];
                            
                            handler.onMessage(new IrcMessage(new IrcUser(privmsgSender), privmsgText, new IrcChannel(privmsgChannel)));
                        }
                        
                        break;
                    case "JOIN":
                        handler.onJoin(new IrcUser(line.substring(1, line.indexOf("!"))), new IrcChannel(splittedLine[2]));

                        break;
                    case "KICK":
                        handler.onKick(new IrcUser(splittedLine[3]), new IrcChannel(splittedLine[2]));
                        
                        break;
                    case "MODE":
                        // FIXME: Mode is uncompleted, it just sends the first parameter.
                        handler.onMode(new IrcChannel(splittedLine[2]), splittedLine[3]);
                        
                        break;
                    case "NICK":
                        String oldNick   = line.substring(1, line.indexOf("!"));
                        IrcUser nickUser = new IrcUser(oldNick);
                        nickUser.setNick(splittedLine[2].substring(1));

                        handler.onNickChange(nickUser);

                        break;
                    case "PART":
                        handler.onPart(new IrcUser(line.substring(1, line.indexOf("!"))), new IrcChannel(splittedLine[2]));

                        break;
                }
            }
        }
    }
    
    private void loginToServer(IrcUser user) throws IOException {
        this.changeNick(user.getNick());
        
        writer.write("USER " + user.getLogin() + " 8 * : " + user.getLogin() + ENTER);
        writer.flush();
        
        listenServer();
    }

    public void sendMessage(IrcMessage message) throws IOException {
        writer.write("PRIVMSG " + (message.isPrivate() ? message.getChannel().getName() : message.getReceiver().getNick()) + " :" + message.getText() + ENTER);
        writer.flush();
        
        if(message.getSender() == null)
        	message.setSender(user);

        handler.onMessage(message);
    }
    
    public void changeMode(IrcChannel channel, String mode) throws IOException {
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

    public void partFromChannel(IrcChannel channel) throws IOException {
        writer.write("PART " + channel + ENTER);
        writer.flush();
        
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
