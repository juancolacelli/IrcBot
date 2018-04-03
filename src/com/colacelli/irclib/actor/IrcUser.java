package com.colacelli.irclib.actor;

public class IrcUser {
    private String nick;
    private String login;
    private String oldNick;

    private IrcUser(Builder builder) {
        nick = builder.nick;
        login = builder.login;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String newNick) {
        oldNick = nick;
        nick = newNick;
    }

    public String getLogin() {
        return login;
    }

    public String getOldNick() {
        return oldNick;
    }

    public static class Builder {
        private String nick;
        private String login;

        public Builder setNick(String nick) {
            this.nick = nick;
            return this;
        }

        public Builder setLogin(String login) {
            this.login = login;
            return this;
        }

        public IrcUser build() {
            return new IrcUser(this);
        }
    }
}
