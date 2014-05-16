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
    
    public void connectToServer(IrcServer server, String nick, String login) throws IOException {
        try {
            this.nick     = nick;
            this.login    = login;

            this.server   = server;
            
            socket = new Socket(server.getHostname(), server.getPort());
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            if(!server.getPassword().equals("")) {
                writer.write("PASS " + server.getPassword() + ENTER);
                writer.flush();    
            }
            
            loginToServer(this.nick, this.login);
        } catch(Exception e) {
            reconnectToServer();
        }
    }
    
    public void disconnectFromServer() throws IOException {
        socket.close();
        
        handler.onDisconnect(this.server);
    }
    
    public void joinChannel(String channel) throws IOException {
        writer.write("JOIN " + channel + ENTER);
        writer.flush();

        handler.onJoin(new IrcChannel(channel));
    }
    
    private void listenServer() throws IOException {
        // Keep reading lines from the server.
        String line = null;
        
        while((line = reader.readLine()) != null) {
            System.out.println("<< " + line);

            String[] splittedLine = line.split(" ");
            try {
                int rawCode = Integer.parseInt(splittedLine[1]);
                if(rawCode == RawCode.LOGGED_IN.getCode()) {
                    handler.onConnect(this.server, this.nick, this.login);
                } else if(rawCode == RawCode.NICKNAME_IN_USE.getCode()) {
                    // Re-login with a random ending
                    changeNick(this.nick + (new Random()).nextInt(9));
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
                    case "KICK":
                        String kickNick    = splittedLine[3];
                        String kickChannel = splittedLine[2];
                        
                        handler.onKick(new IrcUser(kickNick), new IrcChannel(kickChannel));
                        
                        break;
                    case "MODE":
                        String modeChannel = splittedLine[2];
                        // FIXME: Mode is uncompleted, it just sends the first parameter.
                        String modeMode    = splittedLine[3];
                        
                        handler.onMode(new IrcChannel(modeChannel), modeMode);
                        
                        break;
                }
            }
        }
    }
    
    private void loginToServer(String nick, String login) throws IOException {
        this.changeNick(nick);
        
        this.login = login;
        
        writer.write("USER " + login + " 8 * : " + login + ENTER);
        writer.flush();
        
        listenServer();
    }

    public void sendMessage(IrcMessage message) throws IOException {
        writer.write("PRIVMSG " + (message.isPrivate() ? message.getChannel().getName() : message.getReceiver().getNick()) + " :" + message.getText() + ENTER);
        writer.flush();    
    }
    
    public void changeMode(String channel, String mode) throws IOException {
        writer.write("MODE " + channel + " " + mode + ENTER);
        writer.flush();
        
        handler.onMode(new IrcChannel(channel), mode);
    }
    
    public void changeNick(String nick) throws IOException {
        this.nick = nick;
        
        writer.write("NICK " + nick + ENTER);
        writer.flush();
    }

    public void partFromChannel(String channel) throws IOException {
        writer.write("PART " + channel + ENTER);
        writer.flush();
        
        handler.onPart(new IrcChannel(channel));
    }
    
    public void reconnectToServer() throws IOException {
        disconnectFromServer();
        connectToServer(this.server, this.nick, this.login);
    }
}
