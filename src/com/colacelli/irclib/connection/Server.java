package com.colacelli.irclib.connection;

public class Server {
    private String hostname;
    private int port;
    private boolean secure;
    private String password;

    private Server(Builder builder) {
        hostname = builder.hostname;
        port = builder.port;
        secure = builder.secure;
        password = builder.password;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public String getPassword() {
        return password;
    }

    public boolean isSecure() {
        return secure;
    }

    public static class Builder {
        private String hostname;
        private int port;
        private boolean secure;
        private String password;

        public Builder setHostname(String hostname) {
            this.hostname = hostname;
            return this;
        }

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public Builder setSecure(boolean secure) {
            this.secure = secure;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Server build() {
            return new Server(this);
        }
    }
}
