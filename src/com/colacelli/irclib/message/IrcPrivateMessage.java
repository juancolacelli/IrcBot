package com.colacelli.irclib.message;

import com.colacelli.irclib.actor.IrcUser;

public class IrcPrivateMessage extends IrcMessage {
    protected IrcUser receiver;

    private IrcPrivateMessage(Builder builder) {
        sender = builder.sender;
        receiver = builder.receiver;
        text = builder.text;
    }

    public IrcUser getReceiver() {
        return receiver;
    }

    public void setReceiver(IrcUser receiver) {
        this.receiver = receiver;
    }

    public static class Builder {
        private IrcUser sender;
        private IrcUser receiver;
        private String text;

        public Builder setSender(IrcUser sender) {
            this.sender = sender;
            return this;
        }

        public Builder setReceiver(IrcUser receiver) {
            this.receiver = receiver;
            return this;
        }

        public Builder setText(String text) {
            this.text = text;
            return this;
        }

        public IrcPrivateMessage build() {
            return new IrcPrivateMessage(this);
        }
    }
}
