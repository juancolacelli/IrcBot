package com.colacelli.irclib;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Random;

public final class IrcConnection {
    private static final String ENTER =  "\r\n";
    private String server;
    private int port;
    private String password;
    
    private String nick;
    private String login;
    
    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;
    private IrcConnectionHandler handler;
    
    public IrcConnection(IrcConnectionHandler handler) {
        this.handler = handler;
        this.handler.setTransport(this);
    }
    
    public void connect(String server, int port, String password, String nick, String login) throws IOException {
        try {
            this.nick     = nick;
            this.login    = login;

            this.server   = server;
            this.port     = port;
            this.password = password;
            
            socket = new Socket(server, port);
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            if(!password.equals("")) {
                writer.write("PASS " + password + ENTER);
                writer.flush();    
            }
            
            login(this.nick, this.login);
        } catch(Exception e) {
            reconnect();
        }
    }
    
    public void disconnect() throws IOException {
        socket.close();
        
        handler.onDisconnect(this.server, this.port);
    }
    
    public void join(String channel) throws IOException {
        writer.write("JOIN " + channel + ENTER);
        writer.flush();

        handler.onJoin(channel);
    }
    
    public void listen() throws IOException {
        // Keep reading lines from the server.
        String line = null;
        
        while((line = reader.readLine()) != null) {
            System.out.println("<< " + line);

            // Login
            if(line.indexOf("004") >= 0) {
                // We are now logged in.  
                handler.onConnect(this.server, this.port, this.nick, this.login);
            } else if(line.indexOf("433") >= 0) {               
                // Re-login with a random ending
                nick(this.nick + (new Random()).nextInt(9));
            }         
            
            if(line.toLowerCase().startsWith("ping ")) {               
                writer.write("PONG " + line.substring(5) + ENTER);
                writer.flush();
                
                handler.onPing();
            } else {
                String[] splittedLine = line.split(" ");
                
                switch(splittedLine[1]) {
                    case "PRIVMSG":
                        int senderIndex  = line.indexOf("!");
                        int messageIndex = line.indexOf(":", 1);
                        
                        if(senderIndex != -1 && messageIndex != -1) {
                            String privmsgSender  = line.substring(1, senderIndex);
                            String privmsgMessage = line.substring(messageIndex + 1);
                            String privmsgChannel = "";
                            
                            if(splittedLine[2].indexOf("#") != -1)
                                privmsgChannel      = splittedLine[2];
                            
                            handler.onMessage(privmsgSender, privmsgMessage, privmsgChannel);
                        }
                        
                        break;
                    case "KICK":
                        String kickNick    = splittedLine[3];
                        String kickChannel = splittedLine[2];
                        
                        handler.onKick(kickNick, kickChannel);
                        
                        break;
                    case "MODE":
                        String modeChannel = splittedLine[2];
                        // FIXME: Mode is uncompleted, it just sends the first parameter.
                        String modeMode        = splittedLine[3];
                        
                        handler.onMode(modeChannel, modeMode);
                        
                        break;
                }
            }
        }
    }
    
    public void login(String nick, String login) throws IOException {
        this.nick(nick);
        
        this.login = login;
        
        writer.write("USER " + login + " 8 * : " + login + ENTER);
        writer.flush();
        
        listen();
    }

    public void msg(String receiver, String message) throws IOException {
        writer.write("PRIVMSG " + receiver + " :" + message + ENTER);
        writer.flush();    
    }
    
    public void mode(String channel, String mode) throws IOException {
        writer.write("MODE " + channel + " " + mode + ENTER);
        writer.flush();
        
        handler.onMode(channel, mode);
    }
    
    public void nick(String nick) throws IOException {
        this.nick = nick;
        
        writer.write("NICK " + nick + ENTER);
        writer.flush();
    }

    public void part(String channel) throws IOException {
        writer.write("PART " + channel + ENTER);
        writer.flush();
        
        handler.onPart(channel);
    }
    
    public void reconnect() throws IOException {
        disconnect();
        connect(this.server, this.port, this.password, this.nick, this.login);
    }
}
