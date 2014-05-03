package com.colacelli;

import java.io.IOException;
import java.util.Random;

public class ConnectionHandler {
	
	private Connection transport;
	
	public ConnectionHandler(Connection transport) {
		this.transport = transport;
	}
	
	public void onConnect() throws IOException {
		transport.login();
	}
	
	public void onLogin() throws IOException {
		transport.join();
	}
	
	public void onLoginError() throws IOException {
		transport.login((new Random()).nextInt(9999) + 1000);
	}
	
	public void onJoin() throws IOException {
		transport.listen();
	}
	
	public void onMessage() {
		// Behavior
	}
}
