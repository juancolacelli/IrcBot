package com.colacelli.irclib.messages;

import com.colacelli.irclib.actors.User;

public class PrivateMessage extends Message {
    protected User receiver;

    private PrivateMessage(Builder builder) {
        sender = builder.sender;
        receiver = builder.receiver;
        text = builder.text;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public static class Builder {
        private User sender;
        private User receiver;
        private String text;

        public Builder setSender(User sender) {
            this.sender = sender;
            return this;
        }

        public Builder setReceiver(User receiver) {
            this.receiver = receiver;
            return this;
        }

        public Builder setText(String text) {
            this.text = text;
            return this;
        }

        public PrivateMessage build() {
            return new PrivateMessage(this);
        }
    }
}
