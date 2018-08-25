package com.colacelli.irclib.connection;

public interface Rawable {
    enum RawCode {
        LOGGED_IN(4),
        NICKNAME_IN_USE(433);

        private final int code;

        RawCode(int newCode) {
            code = newCode;
        }

        public int getCode() {
            return code;
        }
    }
}
