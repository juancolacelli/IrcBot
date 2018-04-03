package com.colacelli.irclib.connection.connectors;

import com.colacelli.irclib.actors.User;
import com.colacelli.irclib.connection.Server;

import java.io.*;
import java.net.Socket;

public class UnsecureConnector extends Connector {
    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;

    @Override
    public void connect(Server newServer, User newUser) throws IOException {
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
