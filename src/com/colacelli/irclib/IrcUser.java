package com.colacelli.irclib;

public class IrcUser {
    private String nick;
    private String login;
    private String oldNick;
    
    public IrcUser(String nick) {
        this.nick = nick;
    }
    
    public IrcUser(String nick, String login) {
        this.nick  = nick;
        this.login = login;
    }
    
    public String getNick() {
        return this.nick;
    }
    
    public String getLogin() {
        return this.login;
    }
    
    public String getOldNick() {
        return this.oldNick;
    }
    
    public void setNick(String nick) {
    	this.oldNick = this.nick;
        this.nick    = nick;
    }
}
