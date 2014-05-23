package com.colacelli.irclib;

public class IrcPrivateMessage extends IrcMessage {
    protected IrcUser receiver;
    
    public IrcPrivateMessage(IrcUser newReceiver, String newText) {
        receiver = newReceiver;
        text     = newText;
    }
    
    public IrcUser getReceiver() {
        return receiver;
    }
}
