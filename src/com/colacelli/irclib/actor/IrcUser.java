package com.colacelli.irclib.actor;

public class IrcUser {
    private String nick;
    private String login;
    private String oldNick;
    
    public IrcUser(String newNick) {
        nick = newNick;
    }

    public IrcUser(String newNick, String newLogin) {
        nick  = newNick;
        login = newLogin;
    }
    
    public String getNick() {
        return nick;
    }
    
    public String getLogin() {
        return login;
    }
    
    public String getOldNick() {
        return oldNick;
    }
    
    public void setNick(String newNick) {
        oldNick = nick;
        nick    = newNick;
    }
}
