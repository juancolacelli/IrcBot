package com.colacelli.irclib.connection.listeners;

public interface Listenable {
    void addListener(OnConnectListener listener);

    void addListener(OnDisconnectListener listener);

    void addListener(OnPingListener listener);

    void addListener(OnJoinListener listener);

    void addListener(OnPartListener listener);

    void addListener(OnKickListener listener);

    void addListener(OnChannelModeListener listener);

    void addListener(OnChannelMessageListener listener);

    void addListener(OnPrivateMessageListener listener);

    void addListener(OnNickChangeListener listener);
}
