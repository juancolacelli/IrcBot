package com.colacelli.irclib.connection.connectors;

import com.colacelli.irclib.actors.User;
import com.colacelli.irclib.connection.Server;

import javax.net.ssl.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;

public class SecureConnector extends Connector {
    final TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }
            }
    };
    private SSLSocket socket;
    private DataOutputStream writer;
    private DataInputStream reader;

    @Override
    public void connect(Server newServer, User newUser) throws IOException {
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        socket = (SSLSocket) factory.createSocket(newServer.getHostname(), newServer.getPort());
        writer = new DataOutputStream(socket.getOutputStream());
        reader = new DataInputStream(socket.getInputStream());

        try {

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
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
        writer.writeBytes(text + ENTER);
        writer.flush();
    }
}
