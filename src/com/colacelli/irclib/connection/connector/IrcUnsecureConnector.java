package com.colacelli.irclib.connection.connector;

import com.colacelli.irclib.actor.IrcUser;
import com.colacelli.irclib.connection.IrcServer;

import java.io.*;
import java.net.Socket;

public class IrcUnsecureConnector extends IrcConnector {
    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;

    @Override
    public void connect(IrcServer newServer, IrcUser newUser) throws IOException {
        socket = new Socket(newServer.getHostname(), newServer.getPort());
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void disconnect() throws IOException {
        writer.close();
        reader.close();
        socket.close();
    }

    @Override
    public String listen() throws IOException {
        return reader.readLine();
    }

    @Override
    public void send(String text) throws IOException {
        writer.write(text + ENTER);
        writer.flush();
    }
}
