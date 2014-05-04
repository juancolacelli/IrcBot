package com.colacelli;

import java.io.IOException;

public class ConnectionHandler {
	private Connection transport;
	
	public ConnectionHandler(Connection transport) {
		this.transport = transport;
	}
	
	public void onConnect(String server, int port) {
		System.out.println("Connecting to " + server + ":" + port);
	}

	public void onDisconnect(String server, int port) {
		System.out.println("Disconnecting from " + server + ":" + port);
	}
	
	public void onLogin(String nick, String login) {
		System.out.println("Logging in as " + nick + ":" + login);
	}
	
	public void onNick(String nick) {
		System.out.println("Changing nickname to " + nick);
	}
	
	public void onJoin(String channel) {
		System.out.println("Joining " + channel);
	}
	
	public void onPart(String channel) {
		System.out.println("Parting from " + channel);
	}
	
	public void onMessage(String sender, String message) throws IOException {
		System.out.println("Message received from " + sender + ": " + message);

		String[] splittedMessage = message.split(" ");
		
		if(splittedMessage[0].equals("!join"))
			transport.join(splittedMessage[1]);
	}
	
	public void onPing() {
		System.out.println("PING!");
	}
	
	public void onKick(String nick, String channel) {
		System.out.println(nick + " has been kicked from " + channel);
	}
}
