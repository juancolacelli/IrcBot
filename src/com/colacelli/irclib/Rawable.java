package com.colacelli.irclib;

public interface Rawable {
    public enum RawCode {
        LOGGED_IN(004),
        NICKNAME_IN_USE(433);
        
    private final int code;
    
    RawCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code; }
    }
}
