package com.colacelli;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class Connection implements Settings {
	
	private Socket socket;
	private BufferedWriter writer;
	private BufferedReader reader;
	private ConnectionHandler handler = new ConnectionHandler(this); 
	
	public void connect() throws UnknownHostException, IOException {
	    socket = new Socket(SERVER, 6667);
	    writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

	    handler.onConnect();
	}
	
	public void login() throws IOException {
		login(0);
	}
	
	public void login(int random) throws IOException {
		String nick = NICK;
		if(random > 0)
			nick += "_" + random;
		
        writer.write("NICK " + nick + "\r\n");
        writer.write("USER " + LOGIN + " 8 * : " + LOGIN + "\r\n");
        writer.flush();
        
        listen();
	}
	
	public void join() throws IOException {
        writer.write("JOIN " + CHANNEL + "\r\n");
        writer.flush();
        
        handler.onJoin();
	}
	
	public void listen() throws IOException {
        // Keep reading lines from the server.
        String line = null;
        while((line = reader.readLine()) != null) {
        	// Login
            if(line.indexOf("004") >= 0) {
                // We are now logged in.
            	handler.onLogin();
            } else if(line.indexOf("433") >= 0) {
            	// Login error
                System.out.println("Nickname is already in use.");

            	handler.onLoginError();
            }         
        	
            if(line.toLowerCase().startsWith("ping ")) {
                // We must respond to PINGs to avoid being disconnected.
            	System.out.println("PING!");
                writer.write("PONG " + line.substring(5) + "\r\n");
                writer.flush();
            } else {
                // Print the raw line received by the bot.
                System.out.println(line);
                
                handler.onMessage();
            }
        }
	}
	
	public void msg(String receiver, String message) throws IOException {
        writer.write("PRIVMSG " + receiver + " :" + message + "\r\n");
        writer.flush();
	}
}
