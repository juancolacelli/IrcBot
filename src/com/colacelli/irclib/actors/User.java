package com.colacelli.irclib.actors;

public class User {
    private String nick;
    private String login;
    private String oldNick;

    private User(Builder builder) {
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

        public User build() {
            return new User(this);
        }
    }
}
