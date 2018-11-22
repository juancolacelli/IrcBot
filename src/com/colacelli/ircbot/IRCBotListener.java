package com.colacelli.ircbot;

import com.colacelli.irclib.connection.Connection;
import com.colacelli.irclib.connection.listeners.*;

public class IRCBotListener implements Listenable {
    protected final Connection connection;

    public IRCBotListener() {
        connection = new Connection();
    }

    @Override
    public void addListener(OnConnectListener listener) {
        connection.addListener(listener);
    }

    @Override
    public void addListener(OnDisconnectListener listener) {
        connection.addListener(listener);
    }

    @Override
    public void addListener(OnPingListener listener) {
        connection.addListener(listener);
    }

    @Override
    public void addListener(OnJoinListener listener) {
        connection.addListener(listener);
    }

    @Override
    public void addListener(OnPartListener listener) {
        connection.addListener(listener);
    }

    @Override
    public void addListener(OnKickListener listener) {
        connection.addListener(listener);
    }

    @Override
    public void addListener(OnChannelModeListener listener) {
        connection.addListener(listener);
    }

    @Override
    public void addListener(OnChannelMessageListener listener) {
        connection.addListener(listener);
    }

    @Override
    public void addListener(OnPrivateMessageListener listener) {
        connection.addListener(listener);
    }

    @Override
    public void addListener(OnChannelNoticeMessageListener listener) {
        connection.addListener(listener);
    }

    @Override
    public void addListener(OnPrivateNoticeMessageListener listener) {
        connection.addListener(listener);
    }

    @Override
    public void addListener(OnNickChangeListener listener) {
        connection.addListener(listener);
    }

    @Override
    public void addListener(OnCtcpListener listener) {
        connection.addListener(listener);
    }

    @Override
    public void addListener(int rawCode, OnRawCodeListener listener) {
        connection.addListener(rawCode, listener);
    }

    @Override
    public void removeListener(OnConnectListener listener) {
        connection.removeListener(listener);
    }

    @Override
    public void removeListener(OnDisconnectListener listener) {
        connection.removeListener(listener);
    }

    @Override
    public void removeListener(OnPingListener listener) {
        connection.removeListener(listener);
    }

    @Override
    public void removeListener(OnJoinListener listener) {
        connection.removeListener(listener);
    }

    @Override
    public void removeListener(OnPartListener listener) {
        connection.removeListener(listener);
    }

    @Override
    public void removeListener(OnKickListener listener) {
        connection.removeListener(listener);
    }

    @Override
    public void removeListener(OnChannelModeListener listener) {
        connection.removeListener(listener);
    }

    @Override
    public void removeListener(OnChannelMessageListener listener) {
        connection.removeListener(listener);
    }

    @Override
    public void removeListener(OnPrivateMessageListener listener) {
        connection.removeListener(listener);
    }

    @Override
    public void removeListener(OnChannelNoticeMessageListener listener) {
        connection.removeListener(listener);
    }

    @Override
    public void removeListener(OnPrivateNoticeMessageListener listener) {
        connection.removeListener(listener);
    }

    @Override
    public void removeListener(OnNickChangeListener listener) {
        connection.removeListener(listener);
    }

    @Override
    public void removeListener(OnCtcpListener listener) {
        connection.removeListener(listener);
    }

    @Override
    public void removeListener(int rawCode, OnRawCodeListener listener) {
        connection.removeListener(rawCode, listener);
    }
}
