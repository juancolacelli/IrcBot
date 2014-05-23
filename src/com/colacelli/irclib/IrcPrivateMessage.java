package com.colacelli.irclib;

public class IrcPrivateMessage extends IrcMessage {
    public IrcPrivateMessage(IrcUser newReceiver, String newText) {
        receiver = newReceiver;
        text     = newText;
    }
}
