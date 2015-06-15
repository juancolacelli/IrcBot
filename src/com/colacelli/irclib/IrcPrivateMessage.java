package com.colacelli.irclib;

public class IrcPrivateMessage extends IrcMessage {
    protected IrcUser receiver;
    
    public IrcPrivateMessage(IrcUser newSender, IrcUser newReceiver, String newText) {
        sender   = newSender;
        receiver = newReceiver;
        text     = newText;
    }

    public IrcUser getReceiver() {
        return receiver;
    }
}
