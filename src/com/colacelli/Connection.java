package com.colacelli;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Random;

public class Connection implements Settings {
	private String server      = SERVER;
	private int port	       = PORT;
	private String password    = PASSWORD;
	
	private String nick        = NICK;
	private String login       = LOGIN;
	private String channel 	   = CHANNEL;
	
	private Socket socket;
	private BufferedWriter writer;
	private BufferedReader reader;
	private ConnectionHandler handler = new ConnectionHandler(this); 
	
	public void connect() throws IOException {
		connect(this.server, this.port);
	}
	
	public void connect(String server, int port) throws IOException {
		try {
			this.server = server;
			this.port   = port;
			
		    socket = new Socket(server, port);
		    writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		    
		    if(!password.equals("")) {
		        writer.write("PASS " + password + "\r\n");
		        writer.flush();	
		    }
		    
		    login();
		    
		    handler.onConnect(this.server, this.port);
		} catch(Exception e) {
			reconnect();
		}
	}
	
	public void disconnect() throws IOException {
		try {
			socket.close();
			
			handler.onDisconnect(this.server, this.port);
		} catch(Exception e) {
			reconnect();
		}
	}
	
	public void reconnect() throws IOException {
		disconnect();
		connect();
	}
	
	public void login() throws IOException {
		login(this.nick, this.login);
	}
	
	public void login(String nick, String login) throws IOException {
		try {
			this.nick(nick);
			
			this.login = login;
			
	        writer.write("USER " + login + " 8 * : " + login + "\r\n");
	        writer.flush();
	        
	        listen();
		} catch(IOException e) {
			reconnect();
		}

	}
	
	public void nick(String nick) throws IOException {
		try {
			this.nick = nick;
			
	        writer.write("NICK " + nick + "\r\n");
	        writer.flush();
		} catch(IOException e) {
			reconnect();
		}
	}
	
	public void join() throws IOException {
		join(this.channel);
	}
	
	public void join(String channel) throws IOException {
		try {
			if(!this.channel.equals(channel))
				part(this.channel);
			
			this.channel = channel;

			writer.write("JOIN " + channel + "\r\n");
	        writer.flush();

	        handler.onJoin(channel);
		} catch(IOException e) {
			reconnect();
		}
	}

	public void part(String channel) throws IOException {
		try {
			this.channel = "";

			writer.write("PART " + channel + "\r\n");
	        writer.flush();
	        
	        handler.onPart(channel);
		} catch(IOException e) {
			reconnect();
		}
	}
	
	public void listen() throws IOException {
        try {
            // Keep reading lines from the server.
            String line = null;
            
	        while((line = reader.readLine()) != null) {
                System.out.println("<< " + line);

	        	// Login
	            if(line.indexOf("004") >= 0) {
	                // We are now logged in.
	            	join();
	
	            	handler.onLogin(this.nick, this.login);
	            } else if(line.indexOf("433") >= 0) {               
	                // Re-login with a random ending
	        		nick(this.nick + (new Random()).nextInt(9));
	            }         
	            
	            if(line.toLowerCase().startsWith("ping ")) {           	
	                writer.write("PONG " + line.substring(5) + "\r\n");
	                writer.flush();
	                
	                handler.onPing();
	            } else {
	            	String[] splittedLine = line.split(" ");
	            	
	            	switch(splittedLine[1]) {
		            	case "PRIVMSG":
			                int senderIndex  = line.indexOf("!");
			                int messageIndex = line.indexOf(":", 1);
			                
			                if(senderIndex != -1 && messageIndex != -1) {
			                    String sender  = line.substring(1, senderIndex);
			                    String message = line.substring(messageIndex + 1);
			                    
			                    handler.onMessage(sender, message);
			                }
			                
		            		break;
		            	case "KICK":
		            		String nick    = splittedLine[3];
		            		String channel = splittedLine[2];
		            		
		            		if(AUTO_REJOIN && nick.equals(this.nick))
		            			join(channel);
		            		
		            		handler.onKick(nick, channel);
		            		
		            		break;
	            	}
	            }
	        }
        } catch(IOException e) {
        	reconnect();
        }
	}
	
	public void msg(String receiver, String message) throws IOException {
		try {
	        writer.write("PRIVMSG " + receiver + " :" + message + "\r\n");
	        writer.flush();	
		} catch(IOException e) {
			reconnect();
		}
	}
}
